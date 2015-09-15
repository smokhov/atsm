#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <unistd.h>
#include <sys/wait.h>


int main(int argc, char** argv)
{
	/*
	 * Just an iterator, like "i"
	 */
	int iArgCount = 1;

	/*
	 * Argument pointer the parent wishes to pass on to its next child
	 */
	char* pcChildArgument = NULL;

	/*
	 * Parent keeps a dynamic array of PIDs of all its children
	 * to wait for them afterwards.
	 */
	pid_t* piChildrenPIDs = NULL;

	/*
	 * Next child's PID
	 */
	pid_t iChildPID;


	/*
	 * Just display some initial info
	 * (taken from argv.c)
	 */
	printf
	(
		"PARENT: No. of args     : %i\n" \
		"PARENT: Our dear selves : %s\n",
		argc,
		argv[0]
	);

	/*
	 * Print the usage info if there are no arguments.
	 */
	if(argc < 2)
	{
		printf("PARENT: usage: ./argvchild arg1 [arg2] ...\n");
		exit(EXIT_FAILURE);
	}

	/*
	 * Allocate as much storage as many arguments are there
	 */
	piChildrenPIDs = (pid_t*)malloc(sizeof(pid_t) * (argc - 1));

	/*
	 * Fork a child per agument and pass this argument to them
	 */
	for(; iArgCount < argc; iArgCount++)
	{
		printf("PARENT: Forking a child for the argument \"%s\"\n", argv[iArgCount]);

		/*
		 * Update the pointer to the next argument so that child inherits it.
		 * Every child will inherit a different pointer
		 */
		pcChildArgument = argv[iArgCount];

		iChildPID = fork();

		if(iChildPID < 0)
		{
			perror("PARENT: fork() failed");
			exit(EXIT_FAILURE);
		}

		/*
		 * At this point child and parent will run different code
		 */

		if(iChildPID == 0)
		{
			/* child */

			printf
			(
				"CHILD [%i]: I am a child and am working with the argument [%s]\n",
				getpid(),
				pcChildArgument
			);

			/* child terminates */
			exit(EXIT_SUCCESS);
		}

		else
		{
			/* parent */

			printf("PARENT: I just forked a child with PID %i\n", iChildPID);

			/* store PID of the just forked child */
			piChildrenPIDs[iArgCount] = iChildPID;
		}
	}

	/*
	 * Parent waits for all the children to terminate
	 */
	for(iArgCount = 1; iArgCount < argc; iArgCount++)
	{
		waitpid(piChildrenPIDs[iArgCount], NULL, 0);
		printf("PARENT: Done waiting for a child with PID %i\n", piChildrenPIDs[iArgCount]);
	}


	/*
	 * This line is printed only after all the children have exited.
	 */
	printf("PARENT: All my legal children have died. Poor 'em.\n");

	return 0;
}

/* EOF */
