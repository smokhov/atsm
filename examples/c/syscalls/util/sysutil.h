/**
 * sysutil.h
 *
 * Header file for System Utilty Functions
 * for the Intro to System Calls Assignment
 *
 * occurs()        - counting
 * readc()         - by Mashrur Mia
 * cls()           - by Arlin Kipling
 * lineCount()     - by Serguei Mokhov
 * eolCount()      - - " -
 * locateLine()    - - " -
 *
 * sysutil.h/.c - combined and documented by Serguei Mokhov
 *
 * Last update:
 *   $Id: sysutil.h,v 1.2 2014/10/26 03:40:19 mokhov Exp $
 */

#ifndef _SYSUTIL_H_
#define _SYSUTIL_H_

int  occurs(char* line, char* str);
char readc(void);          /* Read a char from STDIN and immediately terminate */
void cls(void);            /* Clear screen by calling system(clear)            */
int  eolCount(char* buf);  /* Count a number of EOLs in the buf                */
int  lineCount(char* buf); /* Count a number of lines in the buf               */
                           /* Locate a line number 'no' in the buffer          */
char* locateLine(int no, char* buf, int* bOverBuffer, int* iLineLength);

#endif /* _SYSUTIL_H_ */
