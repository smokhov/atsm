/****
 * ipc.c
 *
 * Generic IPC implementation
 *
 * Serguei A. Mokhov
 */


#include <stdio.h> /* fprintf() */

/* My headers */
#include <ipc.h>
#include <shmem.h>
#include <sockets.h>
#include <npipes.h>
#include <msq.h>


/*
 * My IPC error messages.
 * Index is in ipc_errno of the last error
 */
char* ipc_err_msgs[] =
{
	"No error thank God",
	"Handicapped Request",
	"Malformed Response",
	"Not implemented yet",
	"Memory (memcpy) error",
	"Detected request coming from the server. In this implementation only clients allowed to send requests",
	"In this implementation server is not allowed to receive responses",
	"Attempt to send response from client. In this implementation only server is allowed to send responses",
	"In this implementation client is not allowed to receive requests",
	"Invalid or NULL IPC PACKET (internal communications)",
	"Usupported IPC method. Supported methods are SHMEM, SOCKETS, NPIPES, and MSGQ."
};


/*
 * IPC init routing routine
 */
int
initIPC(ipc_packet_t* ipc_packet)
{
	if(!ipc_packet)
	{
		ipc_errno = INVALID_IPC_PACKET;
		return -1;
	}

	switch(ipc_packet->ipc_method)
	{
		case SHMEM:
			return initIpcShmem(ipc_packet);

		case SOCKETS:
			return initIpcSockets(ipc_packet);

		case NPIPES:
			return initIpcNpipes(ipc_packet);

		case MSGQ:
			return initIpcMsgq(ipc_packet);

		default:
			ipc_errno = UNSUPPORTED_IPC;
			return -1;
	}
}


/*
 * Sending request
 */
int
sendRequest(ipc_packet_t* ipc_packet)
{
	if(!ipc_packet)
	{
		ipc_errno = INVALID_IPC_PACKET;
		return -1;
	}

	if(ipc_packet->reqresp == NULL)
	{
		ipc_errno = INVALID_REQUEST;
		return -1;
	}

	switch(ipc_packet->ipc_method)
	{
		case SHMEM:
			return sendRequestShmem(ipc_packet);

		case SOCKETS:
			return sendRequestSockets(ipc_packet);

		case NPIPES:
			return sendRequestNpipes(ipc_packet);

		case MSGQ:
			return sendRequestMsgq(ipc_packet);

		default:
			ipc_errno = UNSUPPORTED_IPC;
			return -1;
	}
}


/*
 * Receiving request
 */
int
receiveRequest(ipc_packet_t* ipc_packet)
{
	if(!ipc_packet)
	{
		ipc_errno = INVALID_IPC_PACKET;
		return -1;
	}

	if(ipc_packet->reqresp == NULL)
	{
		ipc_errno = INVALID_REQUEST;
		return -1;
	}

	switch(ipc_packet->ipc_method)
	{
		case SHMEM:
			return receiveRequestShmem(ipc_packet);

		case SOCKETS:
			return receiveRequestSockets(ipc_packet);

		case NPIPES:
			return receiveRequestNpipes(ipc_packet);

		case MSGQ:
			return receiveRequestMsgq(ipc_packet);

		default:
			ipc_errno = UNSUPPORTED_IPC;
			return -1;
	}
}


/*
 * Sending response
 */
int
sendResponse(ipc_packet_t* ipc_packet)
{
	if(!ipc_packet)
	{
		ipc_errno = INVALID_IPC_PACKET;
		return -1;
	}

	if(ipc_packet->reqresp == NULL)
	{
		ipc_errno = INVALID_RESPONSE;
		return -1;
	}

	switch(ipc_packet->ipc_method)
	{
		case SHMEM:
			return sendResponseShmem(ipc_packet);

		case SOCKETS:
			return sendResponseSockets(ipc_packet);

		case NPIPES:
			return sendResponseNpipes(ipc_packet);

		case MSGQ:
			return sendResponseMsgq(ipc_packet);

		default:
			ipc_errno = UNSUPPORTED_IPC;
			return -1;
	}
}


/*
 * Receiving response
 */
int
receiveResponse(ipc_packet_t* ipc_packet)
{
	if(!ipc_packet)
	{
		ipc_errno = INVALID_IPC_PACKET;
		return -1;
	}

	if(ipc_packet->reqresp == NULL)
	{
		ipc_errno = INVALID_RESPONSE;
		return -1;
	}

	switch(ipc_packet->ipc_method)
	{
		case SHMEM:
			return receiveResponseShmem(ipc_packet);

		case SOCKETS:
			return receiveResponseSockets(ipc_packet);

		case NPIPES:
			return receiveResponseNpipes(ipc_packet);

		case MSGQ:
			return receiveResponseMsgq(ipc_packet);

		default:
			ipc_errno = UNSUPPORTED_IPC;
			return -1;
	}
}


/*
 * Finish IPC communication (clean up)
 */
int
finishIPC(ipc_packet_t* ipc_packet)
{
	if(!ipc_packet)
	{
		ipc_errno = INVALID_IPC_PACKET;
		return -1;
	}

	switch(ipc_packet->ipc_method)
	{
		case SHMEM:
			return finishIpcShmem(ipc_packet);

		case SOCKETS:
			return finishIpcSockets(ipc_packet);

		case NPIPES:
			return finishIpcNpipes(ipc_packet);

		case MSGQ:
			return finishIpcMsgq(ipc_packet);

		default:
			ipc_errno = UNSUPPORTED_IPC;
			return -1;
	}
}


/*
 * A somewhat analogous to perror(), but for this module
 */
void
ipcerror(char* msg)
{
	fprintf(stderr, "[%d] %s: %s\n", ipc_errno, msg, ipc_err_msgs[ipc_errno]);
}


/* EOF */
