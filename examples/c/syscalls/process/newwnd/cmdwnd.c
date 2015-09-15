/*
 * cmdwnd.c
 *
 * Execute a command in a new window and wait for it to
 * terminate (prompt the user to type in 'Q' or 'q').
 *
 * Serguei A. Mokhov, mokhov@cs.concordia.ca
 * Updated on: $Id: cmdwnd.c,v 1.2 2014/10/26 03:40:19 mokhov Exp $
 */

#include "sysutil.h" /* readc() */

#include <sys/types.h>
#include <signal.h>
#include <sys/wait.h>
#include <unistd.h>
#include <stdio.h>
#include <ctype.h>
#include <stdlib.h>

int
main(int argc, char* argv[])
{
	/* special case */
	if(argc == 2) /* this part executes in a new window */
	{
		pid_t PID = fork();

		if(PID < 0)
		{
			perror("fork(cmd) failed");
			exit(1);
		}

		if(PID == 0) /* child executs the desired command */
		{
			execlp(argv[1], argv[1], NULL);
			perror("execlp(cmd) failed");
		}
		else /* parent window simply waits */
		{
			wait(NULL);
			printf("Enter q to quit");
			fflush(stdout);
			while(tolower(readc()) != 'q');
		}
	}

	else /* this part executes in the original window */
	{
		pid_t PID = fork(); /* spawn a child for the new window */

		if(PID < 0)
		{
			perror("fork(wnd) failed");
			exit(1);
		}

		if(PID == 0) /* child runs a window (xterm) */
		{
			/* We execute ourself with a special option, which is */
			/* a command name, e.g. 'cal' - the calendar */
			execlp("xterm", "xterm", "-e", "./cmdwnd", "cal", NULL);
			/*      ^^^^^    ^^^^^    ^^    ^^^^^^^^    ^^    ^^^^*/
			/*      window   again  option  us         cmd  always*/

			perror("execlp(wnd) failed");
		}
		else /* Original window can either wait to not wait, up to you. */
		{
			wait(NULL);
		}
	}

	return 0;
}

/* EOF */
