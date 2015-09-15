/****
 * microsh.c
 *
 * Main module of the Microshell Client
 *
 * Serguei A. Mokhov
 */

/* My includes */
#include <microsh.h>
#include <msh_parse.h>
#include <msh_string.h>
#include <ipc.h>
#include <sem.h>
#include <pipeline.h>

/* Standard Headers */
#include <string.h>
#include <unistd.h> /* syscalls */
#include <stdio.h>
#include <stdlib.h> /* exit() */
#include <sys/wait.h>

#include <signal.h>
#include <setjmp.h>


/* Signal Stuff */
void initSignals(void);
static void onAlarm(int signo);
static void onAckTimeout(int signo);


/* To restart from a slow system call */
static sigjmp_buf jmpbuf;


/* Global status structure for the shell. */
/* See definition in microsh.h            */
sh_status_t status;

response_t response; /* A ptr to our private mem seg. */

/* For internal comm */
ipc_packet_t ipc_packet_data;


/**
 * Function: main()
 * Description: main routine of the shell.
 * Synopsis: int main(int argc, char* argv[]) (conforms to ANSI C)
 * Return value:
 *   o An integer, indicating exit status.
 */
int
main(int argc, char* argv[])
{
	char* prompts[] =
	{
		"msh",
		"rshell",
		"rsnoop"
	};

	enum
	{
		P_MSH,
		P_RSHELL,
		P_RSNOOP
	};

	int prompt = P_MSH;

	printf
	(
		"\nWelcome to Microshell Client\nVersion: %s\nType '?' for help.\n",
		MICROSHELL_VERSION
	);

	memset(&status, 0, sizeof(sh_status_t));
	init();

	/* We loop until we get the quit signal */
	while(status.curr_signal != SH_QUIT)
	{
		char        cmdline[CMD_LINE_MAX_CHARS] = {0};
		/*sh_commands cmd_type;*/

		/* Save our state here to restart from here
		 * in case we got interrupted by a SIGALRM
		 */
		if(sigsetjmp(jmpbuf, SIGALRM))
		{
		}

		/* Prompt */
		printf("\n%s> ", prompts[prompt]);

		fgets(cmdline, CMD_LINE_MAX_CHARS, stdin);

		/* A copy of a command line for processing */
		strcpy(status.cmdline, cmdline);

		if(status.curr_signal == SH_REMOTE)
		{
			if(prompt == P_RSHELL)
				rshell(cmdline);
			else
				rsnoop(cmdline);
		}
		else
		{
			/* Parse the command line */
			status.curr_cmd = parseCmd(cmdline);

			prompt = P_MSH;

			/* Dispatch according to the command type */
			switch(status.curr_cmd)
			{
				case QUIT:
					status.curr_signal = SH_QUIT;
					/*disconnect();*/
					break;

				case SRV_RSHELL:
					/*rshell(cmdline);*/
					connectSnoopSrv(cmdline);
					prompt = P_RSHELL;
					break;

				case SRV_RSNOOP:
					/*rsnoop(cmdline);*/
					connectSnoopSrv(cmdline);
					prompt = P_RSNOOP;
					break;

				case REGISTER: /* Send connection request */

					if(status.connected == true)
						fprintf(stderr, "This client already opened a connection to the server!\n");
					else
					{
						connectTimeSrv();
						initSignals();
					}

					break;


				case SRV_VERSION: /* Send version request */

					if(status.connected == false)
						fprintf(stderr, "Server Version Request: Not connected to the server yet.\n");
					else
						srvVersion();

					break;


				case SRV_ELAPSED_TIME: /* Send eslsapsed time request */

					if(status.connected == false)
						fprintf(stderr, "Elapsed Time Request: Not connected to the server yet.\n");
					else
						srvElapsedTime();

					break;


				case SRV_TOD:/* Send time-of-the-day request */
					if(status.connected == false)
						fprintf(stderr, "Time of the Day Request: Not connected to the server yet.\n");
					else
						srvTOD();

					break;


				case PIPELINE:
					execPipeline(status.pipeline_size, -1);
					reset();
					break;


				case HELP:
					showHelp();
					break;


				case BLANK:
				case UNKNOWN: /* UNKNOWN was meant for error checking. Unused. */
					reset();
					break;


				default:
					fprintf(stderr, "msh: Unexpected command type: %d (internal error)\n", status.curr_cmd);
					exit(1);
			} /* switch(status.curr_cmd) */
		} /* !SH_REMOTE */

		/* Make sure we don't have anything in those streams */
		fflush(stdout);
		fflush(stdin);
		fflush(stderr);

	} /*  while(not QUIT) */

	exit(0);

} /* main() */


void
reset(void)
{
	if(status.redirect_file != NULLPTR)
		free(status.redirect_file);

	if(status.cmd_argvs != NULL)
	{
		int i = 0;

		for(; i < status.pipeline_size + 1; i++)
			free(status.cmd_argvs[i]);

		free(status.cmd_argvs);

		status.cmd_argvs = NULL;
	}

	status.curr_signal   = SH_LISTENING;
	status.redirection   = false;
	status.redirtype     = false;
	status.pipeline_size = 0;
	status.redirect_file = NULLPTR;
}


/**
 * Function:    init()
 * Description: initializes all global status variables to their defaults.
 * Synopsis:    void init(void)
 * Notes:
 *   o Must be invoked at the very beginning of the program, or
 *     after cleanUp(). Never in between.
 */
void
init(void)
{
	reset();

	/* On exit handler */
	if(atexit(cleanUp) < 0)
		fprintf(stderr, "microsh::init():atexit() - failed!\n");
}


/**
 * Function:    cleanUp()
 * Descritpion: cleans up all global status variables.
 * Synopsis:    void cleanUp(void)
 * Notes:
 *   o assumes all pointers to the dynamic memory are valid.
 *   o calls init() a the end.
 */
void
cleanUp(void)
{
	reset();

	if(status.connected == true)
		disconnect();
}


/*
 * Just display a help on available commands
 */
void
showHelp(void)
{
	printf
	(
		"Command List:\n" \
		"-------------\n\n" \
		"?, help                 - to display this screen\n\n" \
		"q, quit, terminate,     - to quit the shell client\n" \
		"close, disconnect, exit\n\n" \
		"open, connect, register - register client within the server\n\n" \
		"srvver, server version  - to request server's version\n\n" \
		"eltime, elapsed time    - to request elapsed time\n\n" \
		"tod, time of the day    - to request time of the day\n\n" \
		"Any other command will be treated as external.\n"
	);
}


/*
 * Common request preparation routine
 */
void
requestPrepare(request_t* request)
{
	memset(request, 0, sizeof(request_t));    /* zero it to start with */
	memset(&response, 0, sizeof(response_t)); /* zero response storage */

	request->client_pid  = getpid();                    /* our PID */
	request->resource_id = ipc_packet_data.resource_id; /* our resource ID for the server to attach to */
	request->sem_id      = status.sem_id;               /* Communicate our semaphore to the server */

	ipc_packet_data.reqresp = request;
}


/*
 * Common IPC sending-receiving routine
 */
void
srvSendReceive(void)
{
	if(sendRequest(&ipc_packet_data) < 0)
		ipcerror("microsh::srvSendReceive():sendRequest() - failed");
	else
	{
		int nbytes = 0;
		int n = 0;

		sleep(1); /* yet another nasty kludge */
		          /* looks like we need semaphores again? */
/*		do
*/		{
			ipc_packet_data.reqresp = &response;
			nbytes = receiveResponse(&ipc_packet_data);

			PRINT("nbytes=%d\n", nbytes);

			if(nbytes < 0)
			{
				ipcerror("microsh::srvSendReceive():receiveResponse() - failed");
				/*break;*/
			}
			else
			{
				PRINT("Server's [%d] Response: ", response.srv_pid);
				printf("%s", response.data); fflush(stdout);
				PRINT("\n");
			}

		} /*while(nbytes && n++ != 1);*/

		PRINT("DONE Send/Receive\n");

		if(occurs(response.data, "exit") == 0)
		{
			printf("Exit has been received. Resetting...\n");
			finishIPC(&ipc_packet_data);
			status.connected = false;
			reset();
		}
	}
}


/*
 * Init IPC, connect to the server and register within it.
 */
void
connectTimeSrv(void)
{
	/* Init client's IPC */

	request_t register_cli;

	printf("Intializing IPC... ");

	setupSemaphores();

	ipc_packet_data.ipc_method = SHMEM;
	ipc_packet_data.shmem      = NULL;
	ipc_packet_data.extra      = NULL;
	ipc_packet_data.reqresp    = NULL;
	ipc_packet_data.server     = false;

	if(initIPC(&ipc_packet_data) < 0)
	{
		ipcerror("microsh::connectTimeSrv():initIPC() - failed");
		return; /* this is not fatal, client still may operate as a shell */
	}

	/* Send register request */
	requestPrepare(&register_cli);
	register_cli.command = OPEN;

	if(sendRequest(&ipc_packet_data) < 0)
	{
		ipcerror("microsh::connectTimeSrv():sendRequest() - failed");
		return; /* this is not fatal, client still may operate */
	}

	status.connected = true;
	printf("Done.\n");
}


/*
 * Init IPC, connect to the snoop server
 */
void
connectSnoopSrv(char* host)
{
	/* Init client's IPC */

	printf("Intializing IPC...\n");
	printf("Connecting to host [%s]\n", host);

	ipc_packet_data.ipc_method = SOCKETS;
	ipc_packet_data.shmem      = NULL;
	ipc_packet_data.extra      = NULL;
	ipc_packet_data.reqresp    = host;
	ipc_packet_data.server     = false;

	if(initIPC(&ipc_packet_data) < 0)
	{
		ipcerror("microsh::connectSnoopSrv():initIPC() - failed");
		return; /* this is not fatal, client still may operate as a shell */
	}

	status.connected   = true;
	status.curr_signal = SH_REMOTE;

	printf("Done.\n");

	/*srvVersion();*/

	/* Open a session */
	{
		request_t register_cli;
		requestPrepare(&register_cli);

		register_cli.command = status.curr_cmd == SRV_RSHELL ? RSHELL : RSNOOP;

		srvSendReceive();
	}
}


/*
 * Disconnect from the server.
 */
void
disconnect(void)
{
	request_t           die;
	static volatile int retries;
	struct sigaction    newact, oldact;
	sigset_t            newmask;

	requestPrepare(&die);
	die.command = TERMINATE;

	/* Setup ACK timout handler */
	newact.sa_handler = onAckTimeout;

	/* Set all signals to blocked in the mask */
	sigemptyset(&newact.sa_mask);

	/* No flags */
	newact.sa_flags = 0;

	/* Install SIGALRM handler */
	sigaction(SIGALRM, &newact, &oldact);
	sigemptyset(&newmask);
	sigaddset(&newmask, SIGALRM);


	/* Here we wait for ACK */
/*	for(retries = 0; retries < TIMEOUT_RETRIES; retries++)*/
	{
/*		sigsetjmp(jmpbuf, SIGALRM);*/
		alarm(TIMEOUT);
		srvSendReceive();

/*
		if(semctl(status.sem_id, READER, GETVAL, 0) == -1)
			continue;
		else
			break;
*/
	}

	if(retries == TIMEOUT_RETRIES)
		printf("Sever is not responding. Looks like it's down or tremendously busy. I'm giving up...\n");

	if(finishIPC(&ipc_packet_data) < 0)
		ipcerror("Failed to finish properly IPC");

	if(ipc_packet_data.ipc_method == SHMEM)
		destroySem(status.sem_id);
}


/*
 * Composing the version request.
 */
void
srvVersion(void)
{
	request_t ver;
	requestPrepare(&ver);

	ver.command = VERSION;

	srvSendReceive();
}


/*
 * Request elapsed time
 */
void
srvElapsedTime(void)
{
	request_t etime;
	requestPrepare(&etime);

	etime.command   = TIME;
	etime.time_type = ELAPSED_TIME;

	srvSendReceive();
}


/*
 * Composing the TOD request
 */
void
srvTOD(void)
{
	request_t etime;
	requestPrepare(&etime);

	etime.command   = TIME;
	etime.time_type = TOD;

	srvSendReceive();
}


/*
 * Init signal handling/blocking
 */
void
initSignals(void)
{
	struct sigaction newact, oldact;
	sigset_t         newmask;

	/* To interrogate the server every once in a while */
	newact.sa_handler = onAlarm;

	/* Set all signals to blocked in the mask */
	sigemptyset(&newact.sa_mask);

	/* No flags */
	newact.sa_flags = 0;

	/* Install SIGALRM handler */
	sigaction(SIGALRM, &newact, &oldact);
	sigemptyset(&newmask);
	sigaddset(&newmask, SIGALRM);

	alarm(BUG_SERVER_PERIOD);
}


/*
 * Executes remote shell
 */
void
rshell(char* cmd)
{
	request_t cmdRshell;
	requestPrepare(&cmdRshell);
	cmdRshell.command = RSHELL;
	memcpy(cmdRshell.data, cmd, strlen(cmd) > BSIZE ? BSIZE : strlen(cmd));

	srvSendReceive();
}


/*
 * Playback of what was snooped
 */
void
rsnoop(char* cmd)
{
	request_t cmdRsnoop; /* actually no command is sent */
	requestPrepare(&cmdRsnoop);
	cmdRsnoop.command = RSNOOP;

	srvSendReceive();
}


/*
 * This SIGALRM handler sends PING requests.
 */
static void
onAlarm(int signo)
{
	request_t ping;

	if(status.connected == false) return;

	requestPrepare(&ping);

	ping.command   = PING;
	ping.time_type = ELAPSED_TIME;

	PRINT("ping request [%d]: ", ++status.ping_count);
	srvSendReceive();

	alarm(BUG_SERVER_PERIOD); /* set the timer again */

	/* restart from an interrupted slow sys call */
	siglongjmp(jmpbuf, 1);
}


/*
 * This SIGALRM handler is activated on an ACK timeout
 */
static void
onAckTimeout(int signo)
{
	printf("Server ACK timed out...\n");
	return;
	/*siglongjmp(jmpbuf, 1);*/
}


/*
 * To initialize the semaphores
 */
void
setupSemaphores(void)
{
	/* Only if we are using shared memory IPC */
	if(ipc_packet_data.ipc_method == SHMEM)
	{
		int seminit[2];
		seminit[READER] = 0;
		seminit[WRITER] = 1;

		/* Get a handle on the server's semaphores */
		ipc_packet_data.sem_id = semget(SERVER_KEY, 0, 0);

		if(ipc_packet_data.sem_id == -1)
		{
			perror("microsh::setupSemaphores():semget() - failed");
			exit(1);
		}

		/* Get a couple of semaphores for us */
		status.sem_id = initSemKey(2, IPC_PRIVATE, seminit);
	}
}

/* EOF */
