/*
 * exec_pid.c
 *
 * A simplest example of an exec() syscall.
 *
 * Serguei A. Mohkov, mokhov@cs.concordia.ca
 *
 */
 
#include <unistd.h> /* for exec()   */
#include <stdio.h>  /* for perror() */
#include <stdlib.h> /* for exit()   */

int main(int agrc, char* argv[])
{
	printf("Starting PID: %d\n", getpid());
	printf("Program     : %s\n", argv[0]);
	printf("exec'ing 'ls -l'...\n");
	
	execlp("ls", "ls", "-l", NULL); /* loading a new image - ls */
	
	/* Should never reach here, if it does, */
	/* then the exec() call failed.         */
	
	perror("exec() failed!!!!");
	
	exit(1);
}

/* EOF */
