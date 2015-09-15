/**
 * server.h
 *
 * Header file for common server routines.
 */

#ifndef _SERVER_H
#define _SERVER_H

#include <types.h>
#include <protocol.h>

typedef enum
{
	UNKNOWN,
	TIMESRV,
	SNOOPSRV_DAEMON,
	SNOOPSRV_CHILD
} srv_type_enum;

/* Structure defining "server's status" */
typedef struct _srv_status
{
	bool          just_started; /* A flag to prevent from self-seizing on a startup */
	int           ref_count;    /* Number of "connected" clients */
	int           shm_id;       /* ID of our own memory segment  */
	int           sem_id;
	int           resource_id;
	srv_type_enum srv_type;
	
	void (*init)();
	void (*cleanUp)();
} srv_status_t;


void daemonize(void);

#endif /* _SERVER_H */
