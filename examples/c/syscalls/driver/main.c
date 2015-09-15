/****
 * main.c
 *
 * The ASCII character device driver mini test
 *
 */

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>
#include <unistd.h>

#include "common.h"

int
main(argc, argv)
	int   argc;
	char* argv[];
{
    char buf[BSIZE];
    int fd, i, j, n;

	if((fd = open("/dev/CSI230ASCII", O_RDWR)) >= 0)
	{
		for(i = 10; i <= 60; i += 5)
		{
			n = read(fd, buf, i);

			if(i != n)
			{
				(void) fprintf(stderr, "*** wanted %d bytes, got %d\n", i, n);

				if(n < 0)
					perror("read(CSI230ASCII) failed");
			}
			else
			{
				(void) printf("received:  ");
				for (j = 0; j < n; j++) (void) printf("%c ", buf[j]);
				(void) printf("\n");
			}

			if(i == 30)
			{
				n = write(fd, "BLAH", 4);

				if(n < 0)
					perror("write(/dev/ASCII) failed");
			}
		}

		close(fd);
	}
	else
	{
		perror("open(/dev/CSI230ASCII) failed");
		exit(1);
	}

    exit(0);
}

/* EOF */
