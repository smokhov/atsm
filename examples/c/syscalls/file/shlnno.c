/*
 * shlnno.c
 *
 * A bit more advanced example of using system calls for I/O and of
 * the utility functions. Displays requested line #. Traverses file
 * backwards and forward. Critics, bug reports, and suggestions are
 * more than welcome because the code is not of superb quality.
 *
 * Serguei Mokhov, mokhov@cs.concordia.ca
 * Last update: $Id: shlnno.c,v 1.3 2014/10/26 03:40:19 mokhov Exp $
 *
 * REFERENCES (Linux man pages):
 *
 * System calls:
 *
 * man -S 2 open
 * man -S 2 read
 * man -S 2 write
 * man -S 2 close
 * man lseek
 *
 * String and memory manipulation routines:
 *
 * man atoi
 * man strdup
 * man strcpy
 * man strcmp
 * man strtok
 * man free
 * man memset
 * man perror
 *
 */

#include "sysutil.h"

#include <unistd.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <ctype.h>

extern int errno; /* To analyze errors from syscalls */


#define BSIZE 128

/* Function Prototypes */
void display_line(void); /* Displays a line */

/* Globals */
char buf[BSIZE + 1]; /* read/write buffer for file */
int fd = -1;         /* a file descriptor for the filename */
int arg;             /* option's argument */
int atLineNo;        /* which line we're currently at */
int bufLines;        /* number of lines in the buffer */
int bufLastLineNo;   /* # of the last line in the curr buffer */
int bufFirstLineNo;  /* # of the first line in the curr buffer */
int bufCount;        /* buffer count */
int bBufAligned;     /* flag indicating than the last line in the */
                     /* buffer is aligned to to the buffer's boundary */
ssize_t nbytes;      /* to use with read() and write() syscalls */


/*
 * Main routine
 */
int main(int argc, char* argv[])
{
	char c;                    /* for one-char input from a keyboard */
	char* msg;                 /* a message pointer */
	char inputbuf[BSIZE + 1];  /* buffer for user's input */

	int bOptionsDone = 0;      /* a flag indicating proper input */
	int bQuit = 0;             /* a flag indicating user's desire to quit the program */

	if(argc == 1)
	{
		msg = (char*)strdup("Usage: shlnno filename\n");
		write(STDOUT_FILENO, msg, strlen(msg));
		free(msg);
		exit(0);
	}

	/* Try to open file and see if it exists */
	fd = open(argv[1], O_RDONLY);

	if(fd == -1) /* Show error if any */
		perror(argv[1]);

	/* Pre-read first data chunk */
	nbytes = read(fd, buf, BSIZE);
	bufCount = 1;

	atLineNo = 1;
	bufLines = lineCount(buf);
	bufFirstLineNo = atLineNo;
	bufLastLineNo = bufLines;


	/* Clear screen */
	cls();

	/* Loop until quit */
	while(!bQuit)
	{
		/* Prompt for valid options */
		while(!bOptionsDone)
		{
			char* token; /* a token pointer in the string read from the user */
			int bytesread;

			memset(inputbuf, 0, BSIZE + 1); /* null the entire buffer */

			msg = (char*)strdup
			(
				"Available options: line #\n" \
				"Please provide your options or hit ENTER for default (line 1): "
			);
			write(STDOUT_FILENO, msg, strlen(msg));
			free(msg);

			bytesread = read(STDIN_FILENO, inputbuf, BSIZE);

			/* in case somehow we read 0 bytes, then just keep looping */
			if(bytesread == 0) continue;

			/* set the default */
			if(bytesread == 1 && inputbuf[0] == '\n')
				strcpy(inputbuf, "line 1\n");

			/* Parse the input read */

			/* get rid of last newline */
			if(inputbuf[strlen(inputbuf) - 1] == '\n') inputbuf[strlen(inputbuf) - 1] = '\0';

			/* extract option's name */
			token = (char*)strtok(inputbuf, " \t");

			if(token == NULL) continue;

			if(strcmp(token, "line") != 0)
			{
				strcpy(inputbuf + strlen(token), ": is not a valid option!\n\n");
				write(STDOUT_FILENO, inputbuf, strlen(inputbuf));
				continue;
			}

			/* extract option's value, which is supoposed to be a # */
			token = (char*)strtok(NULL, " \t");

			if(token == NULL)
			{
				msg = (char*)strdup("Missing a value for the option!\n");
				write(STDOUT_FILENO, msg, strlen(msg));
				free(msg);
				continue;
			}

			arg = atoi(token);

			if(errno == ERANGE) /* out of range */
			{
				perror(token);
				continue;
			}

			if(arg > 0)
				break; /* validated, go display */
			else
			{
				msg = (char*)strdup("Line number cannot be negative, zero, or non-numeric!\n");
				write(STDOUT_FILENO, msg, strlen(msg));
				free(msg);
			}
		} /* while options aren't valid */

		cls();

		strcpy(inputbuf, "File: ");
		strcat(inputbuf, argv[1]);
		strcat(inputbuf, "\n\n");
		write(STDOUT_FILENO, inputbuf, strlen(inputbuf));

		/* is the line #arg in the same buf we are at? */
		if(arg >= bufFirstLineNo && arg <= bufLastLineNo)
		{
			display_line();
		}
		else if(arg > bufLastLineNo) /* we move forward */
		{
			int bRightBuffer = 0;

			while(!bRightBuffer)
			{
				nbytes = read(fd, buf, BSIZE);

				if(nbytes == 0)
				{
					msg = (char*)strdup("END OF FILE has been reached!\n");
					write(STDOUT_FILENO, msg, strlen(msg));
					free(msg);
					break;
				}

				bufCount++;

				bufFirstLineNo = bufLastLineNo + 1;

				if(buf[nbytes] != '\n') bBufAligned = 0;
				else bBufAligned = 1;

				bufLastLineNo += eolCount(buf);

				if(arg > bufLastLineNo) continue;
				break;
			}

			if(nbytes) display_line();
		}

		else if(arg < bufFirstLineNo) /* we go backwards */
		{
			int bRightBuffer = 0;

			while(!bRightBuffer)
			{
				if(nbytes != BSIZE)
				{
					/* Take into account that the last chunk of the file may be smaller */
					if(lseek(fd, -(BSIZE + nbytes), SEEK_CUR) == -1)
					{
						perror("lseek(1)");
						exit(0);
					}
				}
				else
				{
					/* In the middle it's safe to assume 2 BSIZE's */
					if(lseek(fd, -2 * BSIZE, SEEK_CUR) == -1)
					{
						perror("lseek(2)");
						exit(0);
					}
				}

				bufLastLineNo -= eolCount(buf);

				nbytes = read(fd, buf, BSIZE);
				bufCount--;

				bufFirstLineNo = bufLastLineNo - eolCount(buf);

				if(arg < bufFirstLineNo) continue;
				break;
			}

			display_line();
		}

		/* Prompt */
		msg = (char*)strdup("\nPress 'q' for quit, 'n' - enter next line.\n");
		write(STDOUT_FILENO, msg, strlen(msg));
		free(msg);

		c = '0';

		while(c != 'q' && c != 'n')
		{
			c = readc();

			switch(tolower(c))
			{
				case 'n':
					break;

				case 'q':
					exit(0);

				default:
					continue;
			}
		}

	} /* while not quit */

	close(fd); /* Good habit to do this even if exit() does it for us. */
 	exit(0);
} /* end of main */


/*
 * Displays requested line
 */
void display_line(void)
{
	/* we might not need to read another buffer in */
	/* unless a line spans accross buffers         */

	int bOverBuffer = 0;       /* indicates if a line spans across buffers */
	int iLineLength;           /* length of a line or its part to be displayed */
	static int adjustment = 0; /* adjustment needed if the last line in prev. buff isn't aligned */
	char* line;                /* a line to be written to STDOUT */

	if(!bBufAligned && bufCount != 1) adjustment = 1;
	else  adjustment = 0;

	line = (char*)strdup(locateLine(arg - bufFirstLineNo + 1 + adjustment, buf, &bOverBuffer, &iLineLength));
	write(STDOUT_FILENO, line, iLineLength);
	free(line);

	if(!bOverBuffer && arg == bufLastLineNo) adjustment = 0;

	/* If line spans across buffers */
	while(bOverBuffer)
	{
		bOverBuffer = 0;

		nbytes = read(fd, buf, BSIZE);

		if(nbytes == 0)
		{
			char* msg = (char*)strdup("END OF FILE has been reached!\n");
			write(STDOUT_FILENO, msg, strlen(msg));
			free(msg);
			return;
		}

		bufCount++;

		line = (char*)strdup(locateLine(1, buf, &bOverBuffer, &iLineLength));
		write(STDOUT_FILENO, line, iLineLength);
		free(line);

		adjustment = 1;
		bufFirstLineNo = bufLastLineNo + 1;
		bufLastLineNo = bufFirstLineNo + eolCount(buf) - 1;
	}

	atLineNo = arg;
}

/* EOF */
