/*
 * fork_exec.c
 *
 * Shows how fork(), exec(), and wait() together
 *
 * Serguei A. Mohkov, mokhov@cs.concordia.ca
 *
 */
 
#include <sys/types.h> /* pid_t                  */
#include <unistd.h>    /* fork(), exec(), wait() */
#include <stdio.h>     /* for perror()           */
#include <stdlib.h>    /* exit() */
#include <sys/wait.h>  /* wait() */

int main(int agrc, char* argv[])
{
	pid_t pid = fork();       /* fork a child */
	printf("PID: %d\n", pid); /* both execute this line */

	if(pid == 0) /* child plays here */
	{
		printf("Hello I'm a child!! My PID=%d\n", getpid());
		printf("Program     : %s\n", argv[0]);
		printf("exec'ing 'ls -l'...\n");

		execlp("ls", "ls", "-l", NULL); /* loading a new image - ls */

		/* Should never reach here, if it does, */
		/* then the exec() call failed.         */
		
		perror("exec() failed!!!");
	
		exit(1);
	}
	else /* parent goes here */
	{
		printf("Hi! I'm a parent (my PID=%d) of the child with PID=%d\n", getpid(), pid);
		printf("I'm waiting for my child to terminate...\n");
		wait(NULL);
		printf("OK, child is dead...\n");
	}
	
	/* might not appear for the child */
	printf("Process with PID=%d terminates...\n", getpid());
	exit(0);
}

/* EOF */
