/*
 * fork_pid.c
 *
 * A simplest example of a fork() syscall.
 *
 * Observation: fork() is called once but returns twice!
 * One return is in parent (child's PID) and another is in child
 * (always 0). Order in which results are printed is not guaranteed.
 *
 * Serguei A. Mohkov, mokhov@cs.concordia.ca
 *
 */
 
#include <sys/types.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>

int main(int argc, char** argv)
{
	pid_t pid = fork();
	printf("PID: %d\n", pid); /* Child will print 0, parent - an ID of the child */
	exit(0); /* Both terminate right after, not very exciting, I know. */
}

/* EOF */
