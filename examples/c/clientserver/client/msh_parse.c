/****
 * msh_parse.c
 *
 * Parsing module of the Microshell Client
 *
 * Serguei A. Mokhov
 */

#include <string.h>
#include <stdio.h>  /* printf() */
#include <stdlib.h> /* calloc() */

#include <common.h>
#include <msh_parse.h>
#include <msh_string.h>

extern struct sh_status status; /* Need to fill some of it in */


/**
 * Function: parseCmd()
 * Description: Main parsing mechanism of the command line
 * Synopsis: sh_commands parseCmd(char* p_cmd)
 * Parameters:
 *   o p_pmd - a char* to a command line from the user.
 * Return value:
 *   o Explicit: Determined command type.
 *   o Implicit: Filled in global status variables, such as:
 *     cmd_argvs, redirection, redirtype, redirect_file, and pipeline_size
 * Notes: does not perform extensive syntax check of a command line
 *        at this point.
 */
sh_commands
parseCmd(char* p_cmd)
{
	char* token;    /* Just a parsed token ptr                        */
	char* currcmd;  /* A ptr to current command to be parsed for args */
	int pipecount;  /* Total # of pipes found                         */
	int paramcount; /* Total # of args for the currcmd found          */
	char* pipeptr;  /* For pipe parsing                               */
	int j;          /* Just a counter                                 */
	int pipeno = 0; /* Another counter                                */

	enum
	{
		LL = 0
	};
	
	char* alias[] =
	{
		"ls -al"
	};
	
	if(isAllBlanks(p_cmd)) return BLANK; /* Do nothing for a blank input line */

	/* Count # of pipes, and allocate memory for them respectively */
	pipecount = pipecnt();
	status.cmd_argvs = (char***)calloc(sizeof(char*), pipecount + 1);
	status.pipeline_size = pipecount;

	/* Get rid of trailing end-of-line */
	if(p_cmd[strlen(p_cmd) - 1] == '\n') p_cmd[strlen(p_cmd) - 1] = '\0';

	/* Was it a user's request to quit? */
	if
	(
		(strcmp(toLowerStr(p_cmd), "q") == 0)
		||
		(strcmp(toLowerStr(p_cmd), "quit") == 0)
		||
		(strcmp(toLowerStr(p_cmd), "terminate") == 0)
		||
		(strcmp(toLowerStr(p_cmd), "close") == 0)
		||
		(strcmp(toLowerStr(p_cmd), "disconnect") == 0)
		||
		(strcmp(toLowerStr(p_cmd), "exit") == 0)
	)
		return QUIT;

	/* Register client within the server */
	if
	(
		(strcmp(toLowerStr(p_cmd), "open") == 0)
		||
		(strcmp(toLowerStr(p_cmd), "connect") == 0)
		||
		(strcmp(toLowerStr(p_cmd), "register") == 0)
	)
		return REGISTER;

	/* Ask server's version */
	if
	(
		(strcmp(toLowerStr(p_cmd), "srvver") == 0)
		||
		(strcmp(toLowerStr(p_cmd), "server version") == 0)
	)
		return SRV_VERSION;

	/* Time */
	if
	(
		(strcmp(toLowerStr(p_cmd), "elapsed time") == 0)
		||
		(strcmp(toLowerStr(p_cmd), "eltime") == 0)
	)
		return SRV_ELAPSED_TIME;

	if
	(
		(strcmp(toLowerStr(p_cmd), "tod") == 0)
		||
		(strcmp(toLowerStr(p_cmd), "time of the day") == 0)
	)
		return SRV_TOD;

	if
	(
		(strcmp(toLowerStr(p_cmd), "help") == 0)
		||
		(strcmp(toLowerStr(p_cmd), "?") == 0)
	)
		return HELP;

	if(occurs(toLowerStr(p_cmd), "rshell") == 0)
	{
		/* Host name */
		currcmd = strtok(p_cmd, BLANK_CHARS);
		currcmd = strtok(NULL, BLANK_CHARS);
		strcpy(p_cmd, currcmd);
		return SRV_RSHELL;
	}

	if(occurs(toLowerStr(p_cmd), "rsnoop") == 0)
	{
		/* Host name */
		currcmd = strtok(p_cmd, BLANK_CHARS);
		currcmd = strtok(NULL, BLANK_CHARS);
		strcpy(p_cmd, currcmd);
		return SRV_RSNOOP;
	}
	
	if(strcmp(toLowerStr(p_cmd), "ll") == 0)
		strcpy(p_cmd, alias[LL]);
	

	currcmd = p_cmd;

	/* Now pipeline parsing */
	{
		/* Hunt for the redirection to a file first */
		char* redirptr = strchr(currcmd, '>');
		char* tailptr  = &currcmd[strlen(currcmd) - 1];

		if(redirptr == NULL)
			status.redirection = false;
		else
		{
			status.redirection = true;
			*redirptr = '\0';

			/* check for double redirection  */
			if((* ++redirptr) == '>')
			{
				*redirptr = '\0';
				redirptr++;
				status.redirtype = true;  /* >> */
			}
			else
				status.redirtype = false; /* >  */

			/* Getting file name ... */

			while(isBlank(*redirptr)) /* avoiding leading blanks  */
				redirptr++;

			while(isBlank(*tailptr))  /* avoiding trailing blanks */
				(* tailptr++) = '\0';

			/* A la strdup() */
			status.redirect_file = calloc(sizeof(char), strlen(redirptr));
			strcpy(status.redirect_file, redirptr);
		}
	}

	/* Parse the rest of the pipeline */

	/* Let's chop down some cmds! */
	while(pipeno != pipecount + 1)
	{
		/* Pipe sym. must be NULLed so strtok() knows where to stop */
		if((pipeptr = strchr(currcmd, '|')) != NULL) *pipeptr = '\0';

		/* Count params and allocate sufficient memory for pointers to them */
		paramcount = paramcnt(currcmd);
		status.cmd_argvs[pipeno] = (char**)calloc(sizeof(char*), paramcount + 2);

		/* Parse command */
		token = strtok(currcmd, BLANK_CHARS);
		status.cmd_argvs[pipeno][0] = token;

		j = 1;

		while((token = strtok(NULL, BLANK_CHARS)) != NULL)
			status.cmd_argvs[pipeno][j++] = token;

		/* Every argv must have a NULL ptr at the last elem */
		status.cmd_argvs[pipeno][j] = NULLPTR;

		/* Next command in the pipeline */
		currcmd = ++pipeptr;
		pipeno++;

	} /* while(some of the commands are in the damn pipeline) */

	return PIPELINE; /* Happy parsing end */
}


/**
 * Function: pipecnt()
 * Description: counts # of pipes in the command line.
 * Synopsis: int pipecnt(void)
 * Return value:
 *   o Number of accounted pipes
 */
int
pipecnt(void) {
	int pipecount = 0;
	char* cmdlineptr = status.cmdline;
	for(; *cmdlineptr; cmdlineptr++) if(*cmdlineptr == '|') pipecount++;
	return pipecount;
}

/**
 * Function: paramcnt()
 * Description: counts # of pipes in the command line.
 * Synopsis: int paramcnt(char* p_cmd)
 * Parameters:
 *   o p_cmd - a char* to the current command to count parameters for
 * Return value:
 *   o Number of accounted parameters. Could be 1 more than real because
 *     of trailing blanks. This, however, does not corrupt anything or
 *     make program unworkable.
 */
int
paramcnt(char* p_cmd) {
	int paramcount = 0;
	char* cmdptr = p_cmd;
	bool blankmode = true; /* to avoid counting consecutive blanks */

	for(; *cmdptr; cmdptr++)
		if(isBlank(*cmdptr) && !blankmode) {
			paramcount++;
			blankmode = true;
		}
		else if (!isBlank(*cmdptr))
			blankmode = false;

	return paramcount;
}

/* EOF */
