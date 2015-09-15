/****
 * msq.h
 *
 * Message Queues IPC Header
 *
 * Serguei A. Mokhov
 */

#ifndef _MSQ_H
#define _MSQ_H

#include <ipc.h>

int initIpcMsgq(ipc_packet_t*);
int sendRequestMsgq(ipc_packet_t*);
int sendResponseMsgq(ipc_packet_t*);
int receiveRequestMsgq(ipc_packet_t*);
int receiveResponseMsgq(ipc_packet_t*);
int finishIpcMsgq(ipc_packet_t*);

#endif /* _MSQ_H */
