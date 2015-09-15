/**
 * server.c
 *
 * Common Server Routines.
 */

#include <server.h>


/* Standard Headers */

#include <stdio.h>     /* printf() */
#include <unistd.h>    /* fork()   */
#include <sys/types.h> /* pid_t, open()    */
#include <sys/stat.h>
#include <fcntl.h>
#include <stdlib.h>    /* exit()   */
#include <string.h>    /* memset() */
#include <sys/times.h> /* times() */
#include <time.h>


/*
 * That's how we become a part of The Daemons Clan
 */
void
daemonize(void)
{
	pid_t reincarnationPID, PID = getpid();

	printf("Entering the World of Daemons, %d...\n", PID);
	fflush(stdout); /* to prevent child printing the above message too */

	reincarnationPID = fork();

	if(reincarnationPID != 0)
	{
		if(reincarnationPID < 0)
		{
			perror("mtimesrv::daemonize() - fork() failed");
			exit(1);
		}
		else
			exit(0); /* parent not here anymore */
	}

	printf("Look around you - they are everywhere!\n");
	printf("Your soul %d has been reincarnated as %d.\n\n", PID, getpid());
	fflush(stdout);

	/* Become a session leader instead of the dead parent */
	setsid();

	/* Clean up on exit handler */
/*	if(atexit(&cleanUp) != 0)
	{
		perror("Failed to establish on-exit signal handler, so manual clean up of resources may be required");
		fflush(stderr);
	}
*/
}
