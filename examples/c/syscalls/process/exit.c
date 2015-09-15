#include <sys/types.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/wait.h>

int
main(int argc, char* argv[])
{
	int iChildStatus;

	pid_t iPID = fork();

	if(iPID == 0)
	{
		printf
		(
			"Child PID(%d) started and is about to exit.\n",
			getpid()
		);

		exit(73);
	}
	
	printf("Parent's wainting for child (%d)\n", iPID);
	
	wait(&iChildStatus);

	printf("Parent: child has exited with code %d\n", WEXITSTATUS(iChildStatus));

	return 0;
}
