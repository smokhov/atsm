#include <stdio.h>

int main(int argc, char** argv)
{
	int iArgCount = 0;
	
	printf
	(
		"No. of args: %i\n" \
		"Us         : %s\n",
		argc,
		argv[0]
	);

	for(; iArgCount < argc; iArgCount++)
		printf("Arg %i: %s\n", iArgCount, argv[iArgCount]);

	return 0;
}
