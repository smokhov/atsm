/****
 * snoopsrv.h
 *
 * Snoop Server Header
 *
 * Serguei A. Mokhov
 */

#ifndef _SNOOPSRV_H
#define _SNOOPSRV_H

#include <server.h>

/* Our version info */
#define SNOOPSRV_VERSION "Snoopserver $Id: snoopsrv.h,v 1.2 2014/11/11 21:35:40 mokhov Exp $ Serguei A. Mokhov"

/* Snoop filename prefix */
#define SNOOPFILE_PREFIX "snoopfile"

/* Shell to spawn and it's options */
#define RSHELL_NAME "/bin/bash"
#define RSHELL_OPTS "--"
/*#define RSHELL_OPTS "-i", "--"*/

/* Snoop Sever Specific Routines */
void init(void);
void cleanUp(void);
void responsePrepare(response_t*, int);
void sendVersion(int);
void registerClient(int socket);
void terminate(void);
void rshell(void);
void rsnoop(void);

#endif /* _SNOOPSRV_H */
