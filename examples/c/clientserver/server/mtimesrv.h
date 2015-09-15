/****
 * mtimesrv.h
 *
 * Header file for the main module of the Micro Time Server
 *
 * Serguei A. Mokhov
 */

#ifndef _MTIMESRV_H
#define _MTIMESRV_H

#include <server.h>

/* Our version info */
#define MTIMESRV_VERSION "Time Server $Id: mtimesrv.h,v 1.2 2014/11/11 21:35:40 mokhov Exp $ Serguei A. Mokhov"


/* Micro Time Sever Specific Routines */

void setupSemaphores(void);
void sendTime(void);
void sendVersion(void);
void processPing(void);

void init(void);
void cleanUp(void);

void responsePrepare(response_t*);

void registerClient(void);
void terminate(void);


#endif /* _MTIMESRV_H */
