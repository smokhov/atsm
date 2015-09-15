/****
 * npipes.h
 *
 * Named Pipes (FIFOs) IPC Header
 *
 * Serguei A. Mokhov
 */

#ifndef _NPIPES_H
#define _NPIPES_H

#include <ipc.h>

int initIpcNpipes(ipc_packet_t*);
int sendRequestNpipes(ipc_packet_t*);
int sendResponseNpipes(ipc_packet_t*);
int receiveRequestNpipes(ipc_packet_t*);
int receiveResponseNpipes(ipc_packet_t*);
int finishIpcNpipes(ipc_packet_t*);

#endif /* _NPIPES_H */
