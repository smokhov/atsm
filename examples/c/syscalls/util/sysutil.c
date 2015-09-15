/**
 * sysutil.c
 *
 * System Utilty Functions
 * for the Intro to System Calls
 *
 * occurs()        - 
 * readc()         -
 * cls()           - 
 * lineCount()     - 
 * eolCount()      - 
 * locateLine()    - 
 *
 * sysutil.h/.c
 *
 * Last update: $Id: sysutil.c,v 1.1 2014/09/16 21:25:36 mokhov Exp $
 */

#include "sysutil.h"

#include <unistd.h>    /* syscalls: open(), read(), system(), etc */
#include <sys/types.h> /* systypes */
#include <fcntl.h>
#include <termios.h>   /* struct termios */
#include <stdio.h>     /* perror() */
#include <string.h>
#include <stdlib.h>


/*
 * The function, readc(), reads one character
 * and immediately terminates.
 * The value of the character is returned.
 * This function is used to input one character from
 * the keyboard without having to type Enter after
 * the character.
 */
char
readc()
{
	/* Terminal Interface structures */
	struct termios save; /* backup copy */
	struct termios newt; /* temporrary settings */

	/* a character to be returned */
	char c;

	/* Terminal File Descriptor
	 * static forces the value to be kept accross
	 * separate function calls
	 */
	static int tfd = -1;

	/* Open terminal as read-only if it wasn't yet open */
	if(tfd == -1)
		if((tfd = open("/dev/tty", O_RDONLY)) < 0){
			perror("open(/dev/tty)"); /* report an error if open() failed */
			exit(0); /* terminate */
		}

	/* save previous attributes of the terminal to restore them */
	/* after we read a charcter */
	tcgetattr(tfd, &save);

	/* set up new attributes of the terminal... */
	newt = save;

	/* Turn off ICANON (canonical input expecting EOL) */
	/* and ECHO (so it doesn't print) */
	newt.c_lflag &= (~ICANON);
	newt.c_lflag &= (~ECHO);

	/* Set control characters, so we don't wait for EOLs */
	newt.c_cc[VTIME] = 0;
	newt.c_cc[VMIN] = 1;

	/* set new attributes to the terminal device  according */
	/* to the above settings */
	tcsetattr(tfd, TCSANOW, &newt);

	/* SysCall read() - read exactly one character from the opened terminal */
	read(tfd, &c, 1);

	/* restore previous terminal state */
	tcsetattr(tfd, TCSANOW, &save);

	/* finally, after so much work return the so wanted character */
	return c;
}


/*
 * Clear screen by invocation system() call
 */
void
cls(void)
{
	system("clear");
}


/*
 * Cound # of EOLs in the buf
 */
int eolCount(char* buf)
{
	int i = 0;
	int EOLcount = 0;

	if(!buf) return EOLcount;

	for(; i < strlen(buf); i++) if(buf[i] == '\n') EOLcount++;

	return EOLcount;
}


/*
 * Cound # of lines in the buf. Which may differ from eolCount()
 * by 1.
 */
int
lineCount(char* buf)
{
	return (buf[strlen(buf) - 1] != '\n') ? eolCount(buf) + 1 : eolCount(buf);
}


/*
 * Locates line with # 'no' in the buffer 'buf', and indicates
 * that the line passes the buffer or not in bOverBuffer, and
 * sets the length of the line in 'iLineLength'. Return a pointer
 * to the beggining of the line or NULL if line no is outside valid
 * range or the buf is empty.
 */
char*
locateLine(int no, char* buf, int* bOverBuffer, int* iLineLength)
{
	int i = 0;
	int llcount = 1;
	char* lineptr = NULL;

	if(!buf) return NULL;
	if(no > lineCount(buf)) return NULL;

	for(; i < strlen(buf); i++)
	{
		if(llcount == no)
		{
			lineptr = &buf[i];
			break;
		}
		else if(buf[i] == '\n')
			llcount++;
	}

	for(i = 0; i < strlen(lineptr); i++)
		if(lineptr[i] == '\n') break;

	if(lineptr[i] != '\n')
		*bOverBuffer = 1;

	*iLineLength = i + 1;

	return lineptr;
}


/* returns 0 if the str occurs in line; -1 otherwise */
int
occurs(char* line, char* str)
{
   int   len = strlen(str);
   char* s1;

   s1 = &line[0];

   for(; *s1; s1++)
       if(strncmp(s1, str, len) == 0)
           return 0;

   return -1;
}

/* EOF */
