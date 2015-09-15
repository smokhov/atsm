/****
 * shmem.h
 *
 * Shared Memory IPC Header
 *
 * Serguei A. Mokhov, mokhov@cs.concordia.ca
 */

#ifndef _SHMEM_H
#define _SHMEM_H

#include <ipc.h>

/* How many times retry to lookup a free shmem seg. */
/* Obsolete, I guess */
#define SHMEM_SEG_LOOKUP_THRESHOLD 50

int initIpcShmem(ipc_packet_t*);
int sendRequestShmem(ipc_packet_t*);
int sendResponseShmem(ipc_packet_t*);
int receiveRequestShmem(ipc_packet_t*);
int receiveResponseShmem(ipc_packet_t*);
int finishIpcShmem(ipc_packet_t*);

#endif /* _SHMEM_H */
