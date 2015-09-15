/*
 * cmdwnd-m.c
 *
 * Execute a command in a new window with a help of
 * shell and terminate. Yet another approach. A bit less complex.
 *
 * Mashrur Mia, mia@cs.concordia.ca
 * Created on: March 29, 2002
 * Updated: $Id: cmdwnd-m.c,v 1.2 2014/10/26 03:40:19 mokhov Exp $
 *
 */

#include <sys/types.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/wait.h>

int
main(int argc, char* argv[])
{
	pid_t PID = fork();
	
	if(PID < 0)
	{
		perror("fork() failed");
		exit(1);
	}
	
	if(PID == 0) /* child executes the desired command in a new xterm window */
	{
		/* Run xterm and a tcsh within ordering command's execution */
		execlp("xterm","xterm","-e","tcsh","-c", "cal ; echo -n Press ENTER to quit. ; set v=$<", NULL);
		/* 'cal' command to be replaced doing string disco - of course */
		perror("execlp() failed");
	}
	else /* parent window simply waits or can continue, up to you */
	{
		wait(NULL);
	}

	return 0;
}

/* EOF */
