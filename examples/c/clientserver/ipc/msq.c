/****
 * msq.c
 *
 * Message Queues IPC Implementation
 * Stub Code, yet to be implemented
 *
 * Student:     Serguei A. Mokhov
 */

#include <msq.h>

extern ipc_error_enum ipc_errno;


int
initIpcMsgq(ipc_packet_t* ipc_packet)
{
	ipc_errno = UNIMPLEMENTED;
	return -1;
}


int
sendRequestMsgq(ipc_packet_t* ipc_packet)
{
	ipc_errno = UNIMPLEMENTED;
	return -1;
}


int
sendResponseMsgq(ipc_packet_t* ipc_packet)
{
	ipc_errno = UNIMPLEMENTED;
	return -1;
}


int
receiveRequestMsgq(ipc_packet_t* ipc_packet)
{
	ipc_errno = UNIMPLEMENTED;
	return -1;
}


int
receiveResponseMsgq(ipc_packet_t* ipc_packet)
{
	ipc_errno = UNIMPLEMENTED;
	return -1;
}


int
finishIpcMsgq(ipc_packet_t* ipc_packet)
{
	ipc_errno = UNIMPLEMENTED;
	return -1;
}

/* EOF */
