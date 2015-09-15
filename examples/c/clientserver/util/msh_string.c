/****
 * msh_string.c
 * String utility module
 *
 * Serguei A. Mokhov, mokhov@cs.concordia.ca
 */

#include <common.h> /* BLANK_CHARS */
#include <msh_string.h>

#include <string.h>
#include <ctype.h> /* toupper(), tolower() */

#include <unistd.h>    /* syscalls */
#include <sys/types.h> /* systypes */
#include <fcntl.h>
#include <termios.h>   /* struct termios */
#include <stdio.h>     /* perror() */
#include <stdlib.h>


/**
 * Function: isBlank()
 * Description: checks if a given character is a blank
 * Synopsis: bool isBlank(char c)
 * Parameters:
 *   o c - a char to be checked whether it's blank or not.
 * Return value:
 *   o true if the character is a blank, false otherwise.
 */
bool
isBlank(char c)
{
	int i = 0;
	char* blanks = BLANK_CHARS;

	for(; i < strlen(blanks); i++)
		if(c == blanks[i]) return true;

	return false;
}


/**
 * Function: isAllBlanks()
 * Descritpion: checks if the entire string consist of only blanks
 * Synopsis: bool isAllBlanks(char* str)
 * Parameters:
 *   o str - a char* to be checked.
 * Return value:
 *   o true if the sting is all blanks, false otherwise.
 */
bool
isAllBlanks(char* str)
{
	int i = 0;

	if(!str || (strlen(str) == 0)) return true;

	for(; i < strlen(str); i++)
		if(!isBlank(str[i])) return false;

	return true;
}


/**
 * Function: toLowerStr()
 * Description: forces all possible characters in a string to lower case
 * Synopsis: char* toLowerStr(char* str)
 * Parameters:
 *   o str - a char* to be folded to lower case.
 * Return value:
 *   o a char* to the folded string or NULL if incoming string is NULL
 */
char*
toLowerStr(char* str)
{
	int i = 0;

	if(!str) return NULL;

	for(; i < strlen(str); i++)
		str[i] = tolower(str[i]);

	return str;
}


/**
 * Function: toUpperStr()
 * Description: forces all possible characters in a string to lower case
 * Synopsis: char* toUpperStr(char* str)
 * Parameters:
 *   o str - a char* to be folded to lower case.
 * Return value:
 *   o a char* to the folded string or NULL if incoming string is NULL
 */
char*
toUpperStr(char* str)
{
	int i = 0;

	if(!str) return NULL;

	for(; i < strlen(str); i++)
		str[i] = toupper(str[i]);

	return str;
}


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
	struct termios newt; /* temporary settings */

	/* a character to be returned */
	char c;

	/* Terminal File Descriptor
	 * static forces the value to be kept across
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
	/* after we read a character */
	tcgetattr(tfd, &save);

	/* set up new attributes of the terminal... */
	newt = save;

	/* Turn off ICANON (canonical input expecting EOL) */
	/* and ECHO (so it doesn't print) */
	newt.c_lflag &= (~ICANON);
	newt.c_lflag &= (~ECHO);

	/* Set control characters, so we don't wait for EOLs */
	newt.c_cc[VTIME] = 0;
	newt.c_cc[VMIN]  = 1;

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
 * Count # of EOLs in the buf
 */
int eolCount(char* buf)
{
	int i = 0;
	int EOLcount = 0;

	if(!buf) return EOLcount;

	for(; i < strlen(buf); i++) if(buf[i] == '\n') EOLcount++;

	return EOLcount;
}


/* returns 0 if the str occurs in line; -1 otherwise */
bool
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
