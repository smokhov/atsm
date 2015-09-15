/****
 * sockets.h
 *
 * Sockets IPC Header
 *
 * Serguei A. Mokhov
 */

#ifndef _SOCKETS_H
#define _SOCKETS_H

#include <ipc.h>

/* Number of pending connections queue will hold */
#define BACKLOG 10

int initIpcSockets(ipc_packet_t*);
int sendRequestSockets(ipc_packet_t*);
int sendResponseSockets(ipc_packet_t*);
int receiveRequestSockets(ipc_packet_t*);
int receiveResponseSockets(ipc_packet_t*);
int finishIpcSockets(ipc_packet_t*);

#endif /* _SOCKETS_H */
