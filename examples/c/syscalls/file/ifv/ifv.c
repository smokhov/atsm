/**
 * ifv.c
 *
 * Updated: $Id: ifv.c,v 1.2 2014/09/16 21:25:36 mokhov Exp $
 *
 * NOTES:
 *
 * Disclaimer :-) (I gotta put this in):
 * ----------
 *
 * Please, use it on your own risk, and if you find a problem,
 * let me know. If you can fix it, even better! :)
 *
 * Current known issues (aka BUGS :)):
 * -----------------------------------
 *
 *   - Not quite adequate handling of the EOF.
 *   - Smaller buffer sizes cause going backwards misbehave
 *   - Seeking backwards with lw on does not match to its forward counter part
 *
 */

#include "sysutil.h"

#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <ctype.h>
#include <stdlib.h>
#include <stdio.h>

#include <string.h>

/* Some defines */
#define BSIZE      1024      /* Buffer size */
#define MSG_SIZE   256       /* Message buffer size */
#define SHOW_LINES 20        /* Number of lines per page */
#define BLANKS     " \n\r\t" /* Blanks to trim in strtok() */

/* Struture representing status of IFV */
typedef struct _ifv_status
{
	int     linesOnOff;      /* indicates whether to show line number or no */
	int     strOnOff;        /* if there's a need to match line containg the string */
	char    str[BSIZE];      /* string to be matched against */
	char    buf[BSIZE + 1];  /* buffer to read in file chunks */
	char    filename[BSIZE]; /* to hold a file name being worked on */
	int     page;            /* current page number */
	int     atLineNo;        /* which line we're currently at */
	int     iLinesShown;     /* how many lines were shown in the last page */
	ssize_t nbytes;          /* number of bytes last read in the buf */
	int     bufLastLineNo;   /* # of the last line in the curr buffer */
	int     bufFirstLineNo;  /* # of the first line in the curr buffer */
	int     bufCount;        /* buffer count */
	int     bBufAligned;     /* flag indicating than the last line in the */
                             /* buffer is aligned to to the buffer's boundary */
} ifv_status_t;

/* Enumeration of modes IFV is working in */
typedef enum _ifv_modes
{
	JUST_STARTED, /* Initial startup */
	HALTED,       /* No action or invalid key from the user */
	VIEWING       /* Main mode  - viewing file */
} ifv_modes;


/* This is a global variable indicating IFV's status     */
/* must be declared as extern in other modules/libraries */
ifv_status_t status;


/* Function prototypes */

/* Useful stuff. */

ssize_t msg(char* msg);
void show_top(void);

/* As per asmt rquirements */

void show_next (int file);
void show_next_with_nos (int file);
void show_next_with_str (int file, char *str);
void show_next_with_str_nos (int file, char *str);

void show_prev (int file);
void show_prev_with_nos (int file);
void show_prev_with_str (int file, char *str);
void show_prev_with_str_nos (int file, char *str);

void show_file (char *filename);


/*
 * Main routine
 */
int
main(int argc, char* argv[])
{
	if(argc == 1)
	{
		msg("Usage: ");	msg(argv[0]); msg(" filename\n");
		exit(0);
	}

	memset(&status, 0, sizeof(ifv_status_t)); /* clear status */
	show_file(argv[1]);                       /* make it happen */

	return 0;
}


/*
 * Display the next 20 lines from the current file position of
 * the file whose filehandle is file and return.
 */
void show_next (int file)
{
	int        bOverBuffer  = 0; /* indicates if a line spans across buffers */
	int        iLineLength;      /* length of a line or its part to be displayed */
	static int adjustment   = 0; /* adjustment needed if the last line in prev. buff isn't aligned */
	char*      line;             /* tmp pointer to a line about to be displayed */
	int        llcount      = 0; /* local line counter */
	int        iLinesToShow = SHOW_LINES;

	cls();
	show_top(); /* top banner */

	/*
	 * When pages are aligned to the end of the buffer, we need to
	 * read one more before start playing with it.
	 */
	if(status.atLineNo == status.bufLastLineNo + 1)
	{
		memset(status.buf, 0, BSIZE + 1);
		status.nbytes = read(file, status.buf, BSIZE);

		if(status.nbytes == 0)
		{
			msg("Page-Aligned EOF: No more lines to display.\n");
			return;
		}

		if(status.nbytes < BSIZE) /* force EOL alignment at the end for the last buffer */
		{
			if(status.buf[status.nbytes - 1] != '\n')
				status.buf[status.nbytes] = '\n';
		}

		/* Update status info */
		status.bufCount++;
		status.bufFirstLineNo  = status.bufLastLineNo + 1;
		status.bBufAligned     = (status.buf[status.nbytes - 1] != '\n') ? 0 : 1;
		status.bufLastLineNo  += eolCount(status.buf);
	}

	/* A bit porblmatic place, trying to compute how many lines to display */
	/* (problamatic at the last buffer) */
	if(status.nbytes < BSIZE)
		iLinesToShow = lineCount(status.buf) > SHOW_LINES ? SHOW_LINES : lineCount(status.buf);

	/* show lines up iLinesToShow */
	while(llcount != iLinesToShow)
	{
		int bOccurs = 1; /* this is always 1 for lw set to off    */
		                 /* if lw is on, bOccurs is only = 1 when */
		                 /* pattern occurs in the current line    */

		/* Re-compute adjustment */
		adjustment = (!status.bBufAligned && status.bufCount != 1) ? 1 : 0;

		line = locateLine /* try to locate the line # */
		(
			status.atLineNo - status.bufFirstLineNo + 1 + adjustment,
			status.buf,
			&bOverBuffer,
			&iLineLength
		);

		if(line == NULL)
		{
			msg("No more lines to display.\n");
			break;
		}
		else /* play with copy, not to alter the original */
			line = strdup(line);

		line[iLineLength] = '\0';

		if(status.strOnOff == 1) /* display a line if lw option is on */
		{
			if(occurs(line, status.str) == 0)
			{
				if(status.linesOnOff == 1) /* show line numbers if applicable */
				{
					char numbuf[BSIZE] = {0}; /* to hold an ASCII equivalen of a number */
					sprintf(numbuf, "%d:\t", status.atLineNo);
					msg(numbuf);
				}

				msg(line);
			}
			else { bOccurs = 0;}
		}
		else
		{
			if(status.linesOnOff == 1) /* show line numbers if applicable */
			{
				char numbuf[BSIZE] = {0}; /* to hold an ASCII equivalen of a number */
				sprintf(numbuf, "%d:\t", status.atLineNo);
				msg(numbuf);
			}

			msg(line);
		}

		free(line);

		/* If a line spans across buffers.
		 * A problem exists here: if a lw is on,
		 * previous par of the line might be lost
		 * in the case the pattern wasn't there or
		 * is splitted across buffers.
		 */
		while(bOverBuffer)
		{
			bOverBuffer = 0;

			memset(status.buf, 0, BSIZE + 1);
			status.nbytes = read(file, status.buf, BSIZE);

			if(status.nbytes == 0)
			{
				msg("Overbuffer EOF: No more lines to display\n");
				return;
			}

			status.bufCount++;

			line = (char*)strdup(locateLine(1, status.buf, &bOverBuffer, &iLineLength));

			line[iLineLength] = '\0';

			if(status.strOnOff == 1) /* display a line if lw option is on */
			{
				if(occurs(line, status.str) == 0)
					msg(line);
				else { bOccurs = 0;}
			}
			else
				msg(line);

			free(line);

			if(status.nbytes < BSIZE)
			{
				if(status.buf[status.nbytes - 1] != '\n')
					status.buf[status.nbytes] = '\n';
			}
			else
			{
				status.bufFirstLineNo = status.bufLastLineNo + 1;
				status.bufLastLineNo  = status.bufFirstLineNo + eolCount(status.buf) - 1;
				status.bBufAligned    = (status.buf[status.nbytes - 1] != '\n') ? 0 : 1;
				adjustment            = (!status.bBufAligned && status.bufCount != 1) ? 1 : 0;
			}
		}

		status.atLineNo++;  /* file line index */
		llcount += bOccurs; /* local # of lines */

	} /* while not all lines are shown */

	/* Update status info */
	status.iLinesShown = llcount;
	status.page++;
}


/*
 * Display the next 20 lines with line numbers from the current
 * file position of the file whose filehandle is file and return.
 */
void show_next_with_nos (int file)
{
	status.linesOnOff = 1;
	show_next(file);
}


/*
 * Display the next 20 lines containing the specified string
 * str from the current file position of the file whose filehandle
 * is file and return
 */
void show_next_with_str (int file, char *str)
{
	status.strOnOff = 1;
	strcpy(status.str, str);
	show_next(file);
}


/*
 * Display the next 20 lines containing the specified string str with
 * line numbers from the current file position of the file whose
 * filehandle is file and return
 */
void show_next_with_str_nos (int file, char *str)
{
	status.linesOnOff = 1;
	status.strOnOff   = 1;
	strcpy(status.str, str);
	show_next(file);
}


/*
 * Display the previous 20 lines from the current file position of
 * the file whose filehandle is file and return.
 */
void show_prev (int file)
{
	if(status.page < 2) /* If at the beginning, just re-display it */
	{
		status.atLineNo = status.bufFirstLineNo;
		status.page--;
		show_next(file);
		msg("\nAt the beginning of the file.\n");
		return;
	}

	status.page -= 2; /* two --, because it'll be ++'ed in show_next() */

	/* How many line we should go backwards. */
	/* TODO: Should be redone to work properly with occurs() */
	status.atLineNo -= status.iLinesShown == SHOW_LINES ? 2 * SHOW_LINES : status.iLinesShown + SHOW_LINES;

	while(status.atLineNo < status.bufFirstLineNo)
	{
		if(status.nbytes < BSIZE) /* in case of a last buffer we seek less */
		{
			if(lseek(file, -(status.nbytes + BSIZE), SEEK_CUR) == -1)
			{
				perror("show_prev:lseek(nbytes+BSIZE) - failed");
				exit(1);
			}
		}
		else /* nurmally we seek 2 buffers backwards */
			if(lseek(file, -(BSIZE * 2), SEEK_CUR) == -1)
			{
				perror("show_prev:lseek(2*BSIZE) - failed");
				exit(1);
			}

		/* Re-count position "indices", and pre-read buffer for examination */
		status.bufLastLineNo -= eolCount(status.buf);
		status.nbytes = read(file, status.buf, BSIZE);
		status.bufCount--;
		status.bufFirstLineNo = status.bufLastLineNo - eolCount(status.buf);
	}

	show_next(file);
}


/*
 * Display the previous 20 lines with line numbers from the current file
 * position of the file whose filehandle is file and return.
 */
void show_prev_with_nos (int file)
{
	status.linesOnOff = 1;
	show_prev(file);
}


/*
 * Display the previous 20 lines containing the specified string str from
 * the current file position of the file whose filehandle is file and return.
 */
void show_prev_with_str (int file, char *str)
{
	status.strOnOff = 1;
	strcpy(status.str, str);
	show_prev(file);
}


/*
 * Display the previous 20 lines containing the specified string str with
 * line numbers from the current file position of the file whose filehandle
 * is file and return.
 */
void show_prev_with_str_nos (int file, char *str)
{
	status.linesOnOff = 1;
	status.strOnOff   = 1;
	strcpy(status.str, str);
	show_prev(file);
}


/*
 * Open the file to be displayed (filename) and display the lines.
 */
void show_file(char* filename)
{
	char      cmdline[BSIZE] = {0};           /* command line to hold user's input */
	int       fd;                             /* file descriptor index */
	ssize_t   cmdchars;                       /* # of chars in cmdline for input validation */
	ifv_modes iMode          = JUST_STARTED;  /* mode IFV is working in */

	fd = open(filename, O_RDONLY);

	if(fd == -1)
	{
		perror(filename);
		exit(1);
	}

	/* Pre-read first data chunk */
	status.nbytes = read(fd, status.buf, BSIZE);
	strcpy(status.filename, filename);

	/* Set initial indices */
	status.bufCount       = 1;
	status.atLineNo       = 1;
	status.bufFirstLineNo = status.atLineNo;
	status.bufLastLineNo  = lineCount(status.buf);

	/* Go! */
	cls();
	show_top();

	msg
	(
		"Enter option(s) and/or a blank line to display the file.\n" \
		"Options are:\n" \
		"    q[uit]        - to quit the Interactive File Viewer\n" \
		"    ln on|off     - to turn on/off line numbers display\n" \
		"    lw [<string>] - to display only lines with occurences of <string>;\n" \
		"                    if <string> is blank, the lw mode will be set to off\n" \
		"\n"
	);

	/* Loop until quit */
	while(1)
	{
		int bOptionsDone = 0; /* a flag indicating proper input */

		/* Prompt for valid options */
		while(!bOptionsDone)
		{
			char* token;        /* a token pointer in the string read from the user */
			int   bInputOK = 1; /* a flag saying that input was OK (or not) */

			memset(cmdline, 0, BSIZE + 1); /* null the entire buffer */

			if(iMode == VIEWING) /* Display this prompt only after first startup */
				msg("\nEnter option(s) and/or press 'f' to view forward, 'b' to view backward, or 'q' to quit\n");

			if(iMode != HALTED)
				msg("> "); /* a little convenient command-line prompt */

			if(iMode == VIEWING || iMode == HALTED)
			{
				char cPressedChar = readc();

				iMode = VIEWING;

				switch(tolower(cPressedChar))
				{
					/* Enter options */
					case 'l':
						/* to compensate typed char */
						write(STDIN_FILENO, "l", 1);
						cmdline[0] = 'l';
						/* read the rest of options */
						cmdchars = read(STDOUT_FILENO, cmdline + 1, BSIZE);
						break;

					/* Move forward */
					case 'f':  /* 'f' for forward, per asmt. requirements */
					case 'n':  /* 'n' for next, for convenience */
					case '\n': /* ENTER */
						msg("moving forward...");
						show_next(fd);
						continue;

					/* Move backwards */
					case 'b': /* 'b' for backwards */
					case 'p': /* 'p' for previous */
						msg("moving backwards...");
						show_prev(fd);
						continue;

					/* Quit */
					case 'q': /* 'q' for quit */
					case 'e': /* 'e' for exit :) */
						write(STDIN_FILENO, "q\n", 2);
						cmdline[0] = 'q';
						cmdchars = 1;
						break;

					/* Do nothing on any other key */
					default:
						iMode = HALTED;
						continue;
				}
			}
			else
			{
				/* First read at a start up */
				cmdchars = read(STDOUT_FILENO, cmdline, BSIZE);
			}

			/* in case somehow we read 0 bytes, then just keep looping */
			if(cmdchars == 0) continue;

			/* Parse the input read */

			/* extract option's name */
			token = (char*)strtok(cmdline, BLANKS);

			/* assume default */
			if(token == NULL) {bOptionsDone = 1; break;}

			if(*token == 'q')
			{
				msg("Quitting at user's request\n");
				break;
			}

			/* Parsing loop and validating */
			while(token != NULL && bInputOK)
			{
				if((strcmp(token, "ln") != 0) && (strcmp(token, "lw") != 0))
				{
					msg(token); msg(": is not a valid option!\n\n");
					bInputOK = 0;
					continue;
				}

				if(strcmp(token, "ln") == 0)
				{
					/* extract option's value, which is supoposed to be either 'on' of 'off' */
					token = (char*)strtok(NULL, BLANKS);

					if((strcmp(token, "on") != 0) && (strcmp(token, "off") != 0))
					{
						msg(token); msg(": is not a valid option's value!\n\n");
						bInputOK = 0;
						continue;
					}

					if(strcmp(token, "on") == 0)
						status.linesOnOff = 1;
					else
						status.linesOnOff = 0;
				}

				if(strcmp(token, "lw") == 0)
				{
					/* extract option's value, which is supoposed to be either 'on' of 'off' */
					token = (char*)strtok(NULL, BLANKS);

					if(token == NULL)
					{
						status.strOnOff = 0; /* turning off string match */
						status.str[0] = '\0';
						continue;
					}
					else
					{
						status.strOnOff = 1;
						strcpy(status.str, token);
						lseek(fd, 0, SEEK_SET); /* "rewind" file pointer to the beginning */
					}
				}

				token = (char*)strtok(NULL, BLANKS);
			}

			if(bInputOK) bOptionsDone = 1;
		} /* while options aren't valid */

		if(bOptionsDone) show_next(fd);
		else break;

		iMode = VIEWING;
	} /* while(1) */

	close(fd);
}


/*
 * Just a convenient routine to dipslay a message
 * using write() system call.
 */
ssize_t
msg(char* msg)
{
	if(!msg) return 0;
	return write(STDOUT_FILENO, msg, strlen(msg));
}


/*
 * A function to display top line when viewing a file.
 * Per assignment requirements.
 */
void
show_top(void)
{
	msg("File: "); msg(status.filename); msg("\t\t\t\t\t Options: ln [");
	msg(status.linesOnOff ? "on], lw [" : "off], lw [");
	msg(status.str);
	msg("]\n\n");
}

/* EOF */
