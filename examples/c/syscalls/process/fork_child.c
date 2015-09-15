/*
 * fork_child.c
 *
 * Shows how to distinguish a parent from a child.
 * A bit more interesting example.
 *
 * Serguei A. Mokhov, mokhov@cs.concordia.ca
 *
 */
 
#include <sys/types.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>

int main(int argc, char** argv)
{
	pid_t pid = fork();       /* fork a child */
	printf("PID: %d\n", pid); /* both execute this line */

	if(pid == 0) /* child plays here */
	{
		printf("Hello I'm a child!! My PID=%d\n", getpid());
	}
	else /* parent goes here */
	{
		printf("Hi! I'm a parent (my PID=%d) of the child with PID=%d\n", getpid(), pid);
	}
	
	/* both terminate */
	printf("Process with PID=%d terminates...\n", getpid());
	exit(0);
}

/* EOF */
