/****
 * mtimesrv.c
 * Main module of the Micro Time Server
 *
 * Serguei A. Mokhov
 */

/*
 * TODO:
 *   o go with syslog() instead of redirection and bunch of fflush()es
 */


/* My Headers */

#include <mtimesrv.h>
#include <ipc.h>
#include <sem.h>
#include <file.h>


/* Standard Headers */

#include <stdio.h>     /* printf() */
#include <unistd.h>    /* fork()   */
#include <sys/types.h> /* pid_t, open() */
#include <sys/stat.h>
#include <fcntl.h>
#include <stdlib.h>    /* exit()   */
#include <string.h>    /* memset() */
#include <sys/times.h> /* times() */
#include <time.h>


/* Global variables */

srv_status_t status;          /* Server keeps its status info here */
ipc_packet_t ipc_packet_data; /* For internal inter-module communication */
request_t    request;         /* To keep and process a copy of the received request */


/*
 * Main routine of our mini time server
 */
int
main(int argc, char* argv[])
{
	establishRedirection("mtimesrv.log", REDIR_OUT_APPEND, STDOUT_FILENO | STDERR_FILENO);

	printf("\nMicro Time Server\nVersion: %s\n\n", MTIMESRV_VERSION);

	init();
	daemonize();

	/* main server loop */
	while(1)
	{
		ipc_packet_data.sem_id  = status.sem_id;
		ipc_packet_data.reqresp = &request;
		
		if(receiveRequest(&ipc_packet_data) < 0)
		{
			ipcerror("mtimesrv::main():receiveRequest() - failed");
			exit(1);
		}
		
		/* Dispatch requests */
		switch(request.command)
		{
			case IDLE:
				printf("Idle\n");
				fflush(stdout);
				break;

			case TIME:
				sendTime();
				break;

			case OPEN:
				registerClient();
				break;

			case VERSION:
				sendVersion();
				break;

			case TERMINATE:
				terminate();
				break;

			case PING:
				processPing();
				break;

			default:
				printf("Unsupported command (%d) from a client (PID=%d)... Ignored.\n", request.command, request.client_pid);
				fflush(stdout);
				break;
		}

		/* Reset the request buffer */
		memset(&request, 0, sizeof(request_t));
	}

	return 0;
}


/*
 * Server initialization routine
 */
void
init(void)
{
	printf("Initializing... ");
	fflush(stdout);

	ipc_packet_data.ipc_method = SHMEM;
	ipc_packet_data.server     = true;
	ipc_packet_data.reqresp    = NULL;
	ipc_packet_data.shmem      = NULL;

	setupSemaphores();

	if(initIPC(&ipc_packet_data) < 0)
	{
		ipcerror("mtimesrv::init():initIPC() - failed");
		exit(1);
	}

	status.just_started = true;
	status.ref_count    = 0;
	
	if(ipc_packet_data.ipc_method == SHMEM)
		status.shm_id = ipc_packet_data.resource_id;
	
	printf("Done.\n");
	fflush(stdout);
}


/*
 * Common response preparation routine
 */
void
responsePrepare(response_t* response)
{
	memset(response, 0, sizeof(response_t));

	response->srv_pid = getpid();

	ipc_packet_data.reqresp     = response;
	ipc_packet_data.resource_id = request.resource_id;
}


/*
 * An "open" command from client has been recieved
 */
void
registerClient()
{
	printf
	(
		"Registering client # %d with PID=%d and resource ID=%d... ",
		++status.ref_count,
		request.client_pid,
		request.resource_id
	);
	fflush(stdout);

	if(status.just_started == true) status.just_started = false;

	printf("Done.\n");
	fflush(stdout);
}


/*
 * Terminate request has been received, need to serve it.
 */
void
terminate(void)
{
	response_t ack;

	printf("Processing TERMINATE request from client with PID=%d...\n", request.client_pid);
	fflush(stdout);

	responsePrepare(&ack);
	status.ref_count--;

	/* Send ACK */
	strcpy(ack.data, "ACK");

	sendResponse(&ipc_packet_data);

	printf("Done.\n");
	fflush(stdout);

	if(status.just_started == false && status.ref_count == 0)
	{
		printf("Client with PID=%d was the last bugging client.\n", request.client_pid);
		printf("Shutting down the server...\n");
		fflush(stdout);
		exit(0);
	}
}


/*
 * Respond to the time request
 */
void
sendTime(void)
{
	response_t rtime;

	printf
	(
		"Processing TIME request from client with PID=%d, resource ID=%d, semid=%d...\n",
		request.client_pid,
		request.resource_id,
		request.sem_id
	);
	fflush(stdout);

	responsePrepare(&rtime);

	switch(request.time_type)
	{
		case NONE:
			/* app logic error */
			strcpy(rtime.data, "Application Logic Error: Request for NO time has been received.\n");
			printf("%s", rtime.data);
			fflush(stdout);
			break;

		case ELAPSED_TIME:
		    {
				struct tms times_;
				clock_t boot_time = times(&times_);

				if(boot_time == -1)
				{
					strcpy(rtime.data, "There was an error getting system time. Please come back later and check the server's log.\n");
					perror("mtimesrv::sendTime():times() - failed");
					fflush(stderr);
				}

				sprintf
				(
					rtime.data,
					"Elapsed Time: [%d]=[%.2f], User time: [%d], System time: [%d], Clocks per second: [%d]\n",
					(int)boot_time,
					(float)boot_time / sysconf(_SC_CLK_TCK),
					(int)times_.tms_utime,
					(int)times_.tms_stime,
					(int)sysconf(_SC_CLK_TCK)
				);
		    }
			break;

		case TOD:
			{
				time_t epoch = time(NULL);
				strcpy(rtime.data, ctime(&epoch));
			}
			break;

		default:
			{
				strcpy(rtime.data, "WARNING: Unsupported time type request.\n");
				printf("%s", rtime.data);
				fflush(stdout);
			}
			break;
	}

	if(sendResponse(&ipc_packet_data) < 0)
		ipcerror("mtimesrv::sendTime():sendResponse() - failed");
}


/*
 * Respond to the server version request
 */
void
sendVersion(void)
{
	response_t ver;

	printf
	(
		"Processing VERSION request from client with PID=%d, resource ID=%d, semid=%d...\n",
		request.client_pid,
		request.resource_id,
		request.sem_id
	);
	fflush(stdout);

	responsePrepare(&ver);

	/* Send version text */
	strcpy(ver.data, MTIMESRV_VERSION);

	if(sendResponse(&ipc_packet_data) < 0)
		ipcerror("mtimesrv::sendVersion():sendResponse() - failed");
}


/*
 * When client bug with pings, process them here.
 */
void
processPing(void)
{
	response_t ping;
	struct tms times_;
	clock_t    boot_time;

	printf
	(
		"Processing PING request from client with PID=%d, resource ID=%d, semid=%d...\n",
		request.client_pid,
		request.resource_id,
		request.sem_id
	);
	fflush(stdout);

	responsePrepare(&ping);

	boot_time = times(&times_);

	if(boot_time == -1)
	{
		strcpy(ping.data, "There was an error getting system time. Please come back later and check the server's log.\n");
		perror("mtimesrv::sendTime():times() - failed");
		fflush(stderr);
	}

	sprintf
	(
		ping.data,
		"PING reply: Elapsed Time: [%.2f]\n",
		(float)boot_time / sysconf(_SC_CLK_TCK)
	);

	if(sendResponse(&ipc_packet_data) < 0)
		ipcerror("mtimesrv::processPing():sendResponse() - failed");
}


/*
 * Initial state of our semaphores
 */
void
setupSemaphores(void)
{
	/* For the time being semaphore setup is relevant
	 * to only shared memory IPC
	 */
	if(ipc_packet_data.ipc_method == SHMEM)
	{
		int seminit[2];
		seminit[READER] = 0;
		seminit[WRITER] = 1;

		/* Get a semaphore for us */
		status.sem_id = initSemKey(2, (key_t)SERVER_KEY, seminit);
	}
}


/*
 * Clean up the mess
 */
void
cleanUp(void)
{
	printf("Cleaning up...\n");
	fflush(stdout);

	/* detach all shmem segments here */
	if(ipc_packet_data.ipc_method)
		ipc_packet_data.resource_id = status.shm_id;

	if(finishIPC(&ipc_packet_data) < 0)
	{
		ipcerror("mtimesrv::cleanIp():finishIPC() - failed");
		printf("Terminating abnormally...");
	}

	if(ipc_packet_data.ipc_method == SHMEM)
		destroySem(status.sem_id);

	ipc_packet_data.reqresp = NULL;
	ipc_packet_data.shmem   = NULL;

	printf("Done...\n");
	fflush(stdout);
}

/* EOF */
