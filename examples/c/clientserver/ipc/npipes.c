/****
 * npipes.c
 *
 * Named Pipes (FIFO's) IPC Implementation
 * Stub Code, yet to be implemented
 *
 * Serguei A. Mokhov
 */

#include <npipes.h>

extern ipc_error_enum ipc_errno;


int
initIpcNpipes(ipc_packet_t* ipc_packet)
{
	ipc_errno = UNIMPLEMENTED;
	return -1;
}


int
sendRequestNpipes(ipc_packet_t* ipc_packet)
{
	ipc_errno = UNIMPLEMENTED;
	return -1;
}


int
sendResponseNpipes(ipc_packet_t* ipc_packet)
{
	ipc_errno = UNIMPLEMENTED;
	return -1;
}


int
receiveRequestNpipes(ipc_packet_t* ipc_packet)
{
	ipc_errno = UNIMPLEMENTED;
	return -1;
}


int
receiveResponseNpipes(ipc_packet_t* ipc_packet)
{
	ipc_errno = UNIMPLEMENTED;
	return -1;
}


int
finishIpcNpipes(ipc_packet_t* ipc_packet)
{
	ipc_errno = UNIMPLEMENTED;
	return -1;
}

/* EOF */
