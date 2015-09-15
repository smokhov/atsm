/****
 * msh_parse.h
 *
 * Header file for the parsing module of the Microshell Client
 *
 * Serguei A. Mokhov
 */

#ifndef _MSH_PARSE_H
#define _MSH_PARSE_H

#include <microsh.h> /* sh_commands */

/* Function prototypes of the module. For more info see msh_parse.c */
sh_commands parseCmd(char* p_cmd);
int pipecnt(void);
int paramcnt(char* p_cmd);

#endif /* _MSH_PARSE_H */
