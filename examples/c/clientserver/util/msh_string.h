/****
 * msh_string.h
 *
 * Header file for the string utility module
 *
 * Serguei A. Mokhov
 */

#ifndef _MSH_STRING_H
#define _MSH_STRING_H

#include <types.h> /* bool */

bool isBlank(char c);
bool isAllBlanks(char* str);

char* toLowerStr(char* str);
char* toUpperStr(char* str);

bool occurs(char* line, char* str);
char readc(void);         /* Read a char from STDIN and immediately terminate */
int  eolCount(char* buf); /* Count a number of EOLs in the buf                */

#endif /* _MSH_STRING_H */
