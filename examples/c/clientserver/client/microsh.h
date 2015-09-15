/****
 * microsh.h
 *
 * Header file for the main module of the Microshell Client
 *
 * Serguei A. Mokhov
 */

#ifndef _MICROSH_H
#define _MICROSH_H

/* My headers */
#include <types.h>    /* bool and NULLPTR moved there */
#include <protocol.h>
#include <common.h>   /* some common things down there */

#define MICROSHELL_VERSION "Microshell $Id: microsh.h,v 1.2 2014/11/11 21:43:38 mokhov Exp $ Serguei A. Mokhov"

/* How large the command line could be - 1 */
#define CMD_LINE_MAX_CHARS BSIZE + 1


/* Internal signals that the shell does understand. */
typedef enum
{
	SH_LISTENING, /* Shell is "listening" for user's input            */
	SH_REMOTE,
	SH_QUIT       /* Shell receives a signal to quit                  */
} sh_signals;


/* Defined command (or token) types */
typedef enum
{
	PIPELINE,         /* Pipeline command type                               */
	QUIT,             /* Quit command                                        */
	HELP,             /* Show help                                           */
	REGISTER,         /* Register a client within a server                   */
	SRV_VERSION,      /* Ask for server's version                            */
	SRV_ELAPSED_TIME, /* Reuqest time from the server                        */
	SRV_TOD,          /* Time of the day                                     */
	SRV_RSHELL,
	SRV_RSNOOP,
	UNKNOWN,          /* Command parser was unable to determine command type */
	BLANK             /* Empty input or a line of blanks only                */
} sh_commands;


/* Global status of the shell */
typedef struct sh_status
{
	bool        connected;                   /* indicates that the client connected   */
											 /* to the  server                        */

	int         sem_id;                      /* our sempahore set ID                  */
	int         ping_count;                  /* how many ping requests has been sent  */

	sh_signals  curr_signal;                 /* shell's mode of operation             */
	sh_commands curr_cmd;                    /* type of parsed command                */

   	char***     cmd_argvs;                   /* dynamic container of all agrv's       */
	int         pipeline_size;               /* # of pipes in the pipeline            */
									         /* (less the one of commands)            */

	char        cmdline[CMD_LINE_MAX_CHARS]; /* a working copy of the command line    */

	bool        redirection;                 /* whether redirection present or not    */
	bool        redirtype;                   /* overwrite data ('>') or append ('>>') */
	char*       redirect_file;               /* redirect filename                     */
} sh_status_t;


/* Shell main function prototypes. See doc. in the micorsh.c file */
void init(void);
void reset(void);
void cleanUp(void);
void showHelp(void);

/* IPC */
void setupSemaphores(void);

void disconnect(void);

void requestPrepare(request_t*);
void srvSendReceive(void);

void srvVersion(void);

/* To bug the Micro Time Server */
void connectTimeSrv(void);
void srvPing(void);
void srvElapsedTime(void);
void srvTOD(void);

/* To bug the Snoop Server */
void connectSnoopSrv(char* host);
void rshell(char* cmd);
void rsnoop(char* cmd);

#endif /* _MICROSH_H */
