/****
 * ipc.h
 *
 * Generic IPC Header
 *
 * Serguei A. Mokhov
 */

#ifndef _IPC_H
#define _IPC_H

/* My includes */

#include <types.h>
#include <protocol.h>


/* Data types */

/*
 * Enumeration of error codes
 */
typedef enum
{
	OK = 0,
	INVALID_REQUEST,    /* Malformed request struture */
	INVALID_RESPONSE,   /* Malformed response structure */
	UNIMPLEMENTED,      /* Method not implemented */
	MEMORY_ERROR,       /* Memorry allocation error */
	ERR_SRV_REQUEST,    /* Server is not allowed to make requests in a given implemetatition */
	ERR_SRV_RESPONSE,   /* Server is not allowed to receive response in a given implemetatition */
	ERR_CLI_RESPONSE,   /* Client is not allowed to send responses in a given implemetatition */
	ERR_CLI_REQUEST,    /* Client is not allowed to receive requests in a given implemetatition */
	INVALID_IPC_PACKET, /* Malformed ICP packet structure */
	UNSUPPORTED_IPC     /* Unsupported IPC method requested */
} ipc_error_enum;


/*
 * Analog of errno for this module
 */
ipc_error_enum ipc_errno;


/* Internal communication structure */
typedef struct _icp_packet
{
	ipc_mtd ipc_method;  /* IPC method of communiction to use */
	bool    server;      /* true for server, false for client */
	int     resource_id; /* shmid, fifoid, etc... */
	int     sem_id;      /* ID of a semaphore set */
	void*   reqresp;     /* request_t/response_t pointer to be sent */
	void*   shmem;       /* shmem*, NULL for other IPC means */
	void*   extra;       /* in case if the above is not enough. maybe kludggy */
} ipc_packet_t;


/* Function Prototypes */

int initIPC(ipc_packet_t*);
int sendRequest(ipc_packet_t*);
int sendResponse(ipc_packet_t*);
int receiveRequest(ipc_packet_t*);
int receiveResponse(ipc_packet_t*);
int finishIPC(ipc_packet_t*);

void ipcerror(char*);

#endif /* _IPC_H */
