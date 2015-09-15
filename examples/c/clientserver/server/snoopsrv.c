/****
 * snoopsrv.c
 *
 * Snoop Server Implementation
 *
 * Serguei A. Mokhov
 */

#include <sys/types.h>
#include <termios.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#ifndef	TIOCGWINSZ
#	include <sys/ioctl.h>	/* 44BSD requires this too */
#endif

/* Standard Headers */
#include <stdio.h>     /* printf() */
#include <unistd.h>    /* fork()   */
#include <stdlib.h>    /* exit()   */
#include <string.h>    /* memset() */

/* My Headers */
#include <snoopsrv.h>
#include <ipc.h>
#include <file.h>
#include <tty-pty.h>

/* Globals */

srv_status_t status;          /* Server keeps its status info here */
ipc_packet_t ipc_packet_data; /* For intermal inter-module communication */
request_t    request;         /* To keep and process a copy of the received request */


int
main(int argc, char* argv[])
{
	int newSocketFd;

	/*establishRedirection("snoopsrv.log", REDIR_OUT_APPEND, STDOUT_FILENO | STDERR_FILENO);*/

	printf("\nSnoop Server\nVersion: %s\n\n", SNOOPSRV_VERSION);

	init();

	daemonize();

	/* main server loop */
	for(;;)
	{
		ipc_packet_data.reqresp = &request;

		if((newSocketFd = receiveRequest(&ipc_packet_data)) < 0)
		{
			ipcerror("snoopsrv::main():receiveRequest() - failed");
			exit(1);
		}

		/* Dispatch requests */
		switch(request.command)
		{
			case IDLE:
				printf("Idle\n");
				fflush(stdout);
				break;

			case VERSION:
				sendVersion(newSocketFd);
				break;

			case OPEN: /* after these child server never comes back */
			case RSHELL:
			case RSNOOP:
				registerClient(newSocketFd);
				break;

			case TERMINATE:
				terminate();
				break;

			default:
				printf
				(
				 	"Unsupported command (%d) from a client (PID=%d)... Ignored.\n",
					request.command,
					request.client_pid
				);
				fflush(stdout);
				break;
		}

		/* Reset the request buffer */
		memset(&request, 0, sizeof(request_t));
	}

	cleanUp();
	return 0;
}


void
init(void)
{
	OLOG("Initializing... ");

	memset(&status, 0, sizeof(srv_status_t));
	memset(&ipc_packet_data, 0, sizeof(ipc_packet_t));
	memset(&request, 0, sizeof(request_t));

	ipc_packet_data.ipc_method = SOCKETS;
	ipc_packet_data.server     = true;

	if(initIPC(&ipc_packet_data) < 0)
	{
		ipcerror("snoopsrv.c::init():initIPC() - failed");
		exit(1);
	}

	status.resource_id = ipc_packet_data.resource_id;
	status.srv_type    = SNOOPSRV_DAEMON;

	PRINT("Done.\n");
}


/*
 * Connection from client has been received
 * Parts of the code were inspired by Stevens, CH. 19
 */
void
registerClient(int socket)
{
	pid_t          snoop_child_pid, shell_pid;
	int            fdm;
	struct termios orig_termios;
	struct winsize size;
	char           slave_name[PTSNAME_SIZE];
	int            snoopFd;
	char           snoop_name[BSIZE];
	bool           snoop;

	OLOG
	(
		"Parent Daemon [%d]: Registering client with PID=%d...\n",
		getpid(),
		request.client_pid
	);

	if(status.just_started == true)
		status.just_started = false;

	OLOG("Spawning a Snoop Master Child to handle further communication...\n");

	if((snoop_child_pid = fork()) < 0)  /* Daemon Child The Snoop Master */
		PERREXIT("snoopsrv.c::():fork(snoop child) - failed")

	/* Parent Daemon */
	if(snoop_child_pid != 0)
	{
		close(socket); /* Doesn't need new socket */
		return;        /* Simply returns to accept other clients */
	}

	/* fetch current termios and window size */
	if(tcgetattr(STDIN_FILENO, &orig_termios) < 0)
		PERREXIT("tcgetattr error on stdin")

	if(ioctl(STDIN_FILENO, TIOCGWINSZ, (char*)&size) < 0)
		PERREXIT("TIOCGWINSZ error")

	/* snoop child doesn't need this cause it's not listening */
	close(ipc_packet_data.resource_id);

	OLOG
	(
		"The Snoop Master [%d] spawning shell (%s %s) using pty_fork().\n",
		getpid(),
		RSHELL_NAME,
		RSHELL_OPTS
	);

	/* redirect STDOUT into socket */
	OLOG
	(
		"Snoop Master[%d]: Redirecting socket (%d) to stdout and snoofile (%d)...\n",
		getpid(),
		socket,
		snoopFd
	);

	dup2(socket, STDOUT_FILENO);

	/* establish pty and fork */
	if((shell_pid = pty_fork(&fdm, slave_name, &orig_termios, &size)) < 0)
		PERREXIT("snoopsrv.c::():pty_fork() failed")

	/* Snoop Child's Child: Here we exec the shell */
	if(shell_pid == 0)
	{
		char* argv[] = {RSHELL_NAME, RSHELL_OPTS, NULL};
		execvp(argv[0], argv);

		PERREXIT("can't execute: %s", argv[0]);
	}

	status.srv_type = SNOOPSRV_CHILD;

	/*OLOG("Creating snoopfile \"%s.%d\"\n", SNOOPFILE_PREFIX, request.client_pid);
	sprintf(snoop_name, "%s.%d", SNOOPFILE_PREFIX, request.client_pid);*/
	OLOG("Creating snoopfile \"%s\"\n", SNOOPFILE_PREFIX);
	sprintf(snoop_name, "%s", SNOOPFILE_PREFIX, request.client_pid);

	if(request.command == RSHELL)
	{
		snoopFd = open(snoop_name, O_CREAT | O_WRONLY, S_IRUSR | S_IWUSR);
		snoop = false;
	}
	else /*rsnoop*/
	{
		snoopFd = open(snoop_name, O_RDONLY);
		snoop = true;
	}

	OLOG("Snoop Master[%d]: slave name = %s.\nSetting to raw mode.", getpid(), slave_name);

	if(tty_raw(STDIN_FILENO) < 0) /* user's tty to raw mode */
		PERREXIT("tty_raw error")

	if(atexit(tty_atexit) < 0) /* reset user's tty on exit */
		perror("atexit error");

	OLOG
	(
		"Snoop Master[%d]: Entering pty loop...\n",
		getpid()
	);

	ptyLoop(fdm, 0, snoopFd, socket, snoop); /* copies stdin -> ptym, ptym -> stdout */

	close(snoopFd);

	exit(0); /* Shell on the other end died, so the snoop master exits too */
}


void
cleanUp(void)
{
	PRINT("Cleaning up...\n");

	if(finishIPC(&ipc_packet_data) < 0)
	{
		ipcerror("mtimesrv::cleanIp():finishIPC() - failed");
		PRINT("Terminating abnormally...");
	}

	PRINT("Done...\n");
}


void
responsePrepare(response_t* response, int socket)
{
	memset(response, 0, sizeof(response_t));

	response->srv_pid = getpid();

	ipc_packet_data.reqresp     = response;
	ipc_packet_data.resource_id = socket;
}


void
sendVersion(int socket)
{
	response_t ver;

	OLOG
	(
		"Processing VERSION request from client with PID=%d, resource ID=%d...\n",
		request.client_pid,
		request.resource_id
	);

	responsePrepare(&ver, socket);

	/* Send version text */
	strcpy(ver.data, SNOOPSRV_VERSION);

	if(sendResponse(&ipc_packet_data) < 0)
		ipcerror("mtimesrv::sendVersion():sendResponse() - failed");

	ipc_packet_data.resource_id = status.resource_id;
}


/* Don't look below here */

void
rshell(void)
{
}


void
rsnoop(void)
{
}


void
terminate(void)
{
}

/* EOF */
