/****
 * protocol.h
 *
 * Communication protocol between client and server
 *
 * Serguei A. Mokhov
 */

#ifndef _PROTOCOL_H_
#define _PROTOCOL_H_

#include <sys/types.h>
#include <common.h> /* BSIZE */

/* Badly hardcoded evil security hole :) */
#define SERVER_KEY  0x666 /* share our memory with the devil */
#define SERVER_PORT 0x666 /* for sockets */


/*
 * Enumeration of commands sent to the server by clients
 */
typedef enum
{
	IDLE = 0,     /* Sitting, doing nothing, not really needed now */
	TIME,         /* Regquest time from the server. See time types below */
	OPEN,         /* Register client */
	VERSION,      /* Process version request */
	PING,         /* Process PING request */
	RSHELL,
	RSNOOP,
	TERMINATE     /* Terminate */
} srvcmd;


/*
 * Enumeration of time modes
 */
typedef enum
{
	NONE = 0,     /* We are not interested in time (in the case of open and terminate commands) */
	ELAPSED_TIME, /* Get eplased time fromt the server */
	TOD           /* Get time of the day */
} timetype;


/*
 * Enum. IPC communication methods
 */
typedef enum
{
	SHMEM,   /* Shared Memory */
	SOCKETS, /* Sockets :) */
	NPIPES,  /* Named Pipes (FIFOs)*/
	MSGQ     /* Message Queues */
} ipc_mtd;


/*
 * Client Request data structure
 */
typedef struct _cli_request
{
	char     data[BSIZE]; /* in case we need to communicate some arbitrary data to the server */
	ipc_mtd  ipc_method;  /* Not used now, but for debugging could be used */
	pid_t    client_pid;  /* PID of a client making request */
	int      resource_id; /* private shmem seg of the client */
	int      sem_id;      /* client's semaphore for the shmem seg */
	srvcmd   command;     /* command to communicate to the server */
	timetype time_type;   /* time type requested */
} request_t;


/*
 * Server Response data structure
 */
typedef struct _srv_response
{
	char  data[BSIZE]; /* Actual response data from the server */
	pid_t srv_pid;     /* Server's PID, just for fun or debugging */
} response_t;

#endif /* _PROTOCOL_H_ */
