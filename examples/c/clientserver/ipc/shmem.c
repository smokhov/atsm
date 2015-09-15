/****
 * shmem.c
 *
 * Shared Memory IPC Implementation
 *
 * Serguei A. Mokhov
 */

#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <stdio.h>  /* perror() */
#include <stdlib.h> /* exit() */
#include <string.h> /*memcpy()*/

/* My Headers */
#include <shmem.h>
#include <sem.h>

extern ipc_error_enum ipc_errno;


int
initIpcShmem(ipc_packet_t* ipc_packet)
{
	int shmid;

	if(ipc_packet->server == true) /* server does this */
	{
		/* Create shmem segment */
		if((shmid = shmget((key_t)SERVER_KEY, sizeof(request_t), IPC_CREAT | 0600)) < 0)
		{
			perror("shmem::initIpcShmem:shmget(server)");
			exit(1);
		}

		/* Attach the segment to our data space */
		if((ipc_packet->shmem = shmat(shmid, NULL, 0)) == (char *)-1)
		{
			perror("shmem::initIpcShmem:shmat(server)");
			exit(1);
		}

		ipc_packet->resource_id = shmid; /* for the server to store in its internal status */
	}
	else /* client does this */
	{
		int i;
		int threshold = SHMEM_SEG_LOOKUP_THRESHOLD;

		/* Find the segment sever has allocated */
		if((shmid = shmget((key_t)SERVER_KEY, sizeof(request_t), 0600)) < 0)
		{
			perror("shmem::initIpcShmem:shmget(client)");
			exit(1);
		}

		/* Attach the segment to client's data space */
		if((ipc_packet->shmem = shmat(shmid, NULL, 0)) == (char *) -1)
		{
			perror("shmem::initIpcShmem:shmat(client)");
			exit(1);
		}

		/* try up to threshold times to find a private segment for ourselves */
		for(i = 0; i < threshold; i++)
			if((shmid = shmget(IPC_PRIVATE, sizeof(response_t), 0600)) >= 0) break;

		if(i == threshold)
		{
			printf("shmem::initIpcShmem:shmget(client, IPC_PRIVATE), exceeded threshold (%d), giving up", threshold);
			perror("");
			exit(1);
		}

		if((ipc_packet->extra = shmat(shmid, NULL, 0)) == (char *) -1)
		{
			perror("shmem::initIpcShmem:shmat(client, IPC_PRIVATE)");
			exit(1);
		}

		ipc_packet->resource_id = shmid;
	}

	ipc_errno = OK;
	return 0;
}


int
sendRequestShmem(ipc_packet_t* ipc_packet)
{
	if(ipc_packet->server) /* server may not send requests in this implementation */
	{
		ipc_errno = ERR_SRV_REQUEST;
		return -1;
	}
	else
	{
		request_t* l_shmem;

		((request_t*)ipc_packet->reqresp)->resource_id = ipc_packet->resource_id;

		P(ipc_packet->sem_id, WRITER); /* Make sure no one is writing to server */
		l_shmem = memcpy(ipc_packet->shmem, ipc_packet->reqresp, sizeof(request_t));
		V(ipc_packet->sem_id, READER);

		if(l_shmem == NULL)
		{
			ipc_errno = MEMORY_ERROR;
			return -1;
		}

		return ipc_errno = OK;
	}
}


int
receiveRequestShmem(ipc_packet_t* ipc_packet)
{
	if(ipc_packet->server) 
	{
		request_t* l_shmem = NULL;
		
		printf("READER\n");	fflush(stdout);
		
		P(ipc_packet->sem_id, READER);
		l_shmem = memcpy(ipc_packet->reqresp, ipc_packet->shmem, sizeof(response_t));
		V(ipc_packet->sem_id, WRITER); /* Let the clients know that they can write */
		
		printf("READ stuff\n"); fflush(stdout);
		
		if(l_shmem == NULL)
			return MEMORY_ERROR;
		
		return ipc_errno = OK;
	}
	else /* client may not receive requests in this implementation */
	{
		ipc_errno = ERR_CLI_REQUEST;
		return -1;
	}
}


int
sendResponseShmem(ipc_packet_t* ipc_packet)
{
	if(ipc_packet->server) /* server sends responses */
	{
		/* Attach to client's private segment by the id sent over */
		response_t* l_shmem = shmat(ipc_packet->resource_id, NULL, 0);
		int cli_sem_id = ((request_t*)ipc_packet->shmem)->sem_id;

		if(l_shmem == (response_t*) -1)
		{
			perror("shmem::sendResponseShmem():shmat() - failed");
			exit(1);
		}

		/* Send the response by simply copying it over */
		printf("srv: If nobody else is writing into there, semid=%d\n",cli_sem_id);fflush(stdout);

		P(cli_sem_id, WRITER); /* If nobody else is writing into there */

		printf("srv: Writing...\n");fflush(stdout);
		l_shmem = memcpy(l_shmem, ipc_packet->reqresp, sizeof(response_t));

		printf("srv: V(%d)\n",cli_sem_id);fflush(stdout);
		V(cli_sem_id, READER);
		printf("srv: after V()");fflush(stdout);

		if(l_shmem == NULL)
		{
			ipc_errno = MEMORY_ERROR;
			return -1;
		}

		printf
		(
			"\nResponse (%d) sent: [%d] %s\n\n",
			(int)ipc_packet->reqresp,
			((response_t*)ipc_packet->reqresp)->srv_pid,
			((response_t*)ipc_packet->reqresp)->data
		);

		fflush(stdout);

		/* server detaches every client's space once it's done writing response */
		if(shmdt(l_shmem) < 0)
		{
			perror("shmem::sendResponseShmem():shmdt() - failed");
			exit(1);
		}

		ipc_errno = OK;
		return 0;
	}
	else /* client may not send responses here */
	{
		ipc_errno = ERR_CLI_RESPONSE;
		return -1;
	}
}


int
receiveResponseShmem(ipc_packet_t* ipc_packet)
{
	if(ipc_packet->server) /* server may not receive reponses in this implementation */
	{
		ipc_errno = ERR_SRV_RESPONSE;
		return -1;
	}
	else
	{
		response_t* l_shmem = NULL;
		
		P(ipc_packet->sem_id, READER);
		l_shmem = memcpy(ipc_packet->reqresp, ipc_packet->extra, sizeof(request_t));
		V(ipc_packet->sem_id, WRITER); /* Let the clients know that they can write */
		
		if(l_shmem == NULL)
			return MEMORY_ERROR;
		
		return ipc_errno = OK;
	}
}


int
finishIpcShmem(ipc_packet_t* ipc_packet)
{
	if(ipc_packet->server)
	{
		if(shmdt(ipc_packet->shmem) < 0) /* detach */
			perror("shmem::finishIpcShmem(server):shmdt()");

		/* mark the shmem seg to be destroyed after last detach */
		if(shmctl(ipc_packet->resource_id, IPC_RMID, 0))
			perror("shmem::finishIpcShmem(server):shmctl()");

		ipc_errno = OK;
		return 0;
	}
	else /* client's clean up */
	{
		/* Detach from server's segment */
		if(shmdt(ipc_packet->extra) < 0)
			perror("shmem::finishIpcShmem(client):shmdt(server)");

		/* Detach from our own segment */
		if(shmdt(ipc_packet->shmem) < 0)
			perror("shmem::finishIpcShmem(client):shmdt(client)");

		/* Mark our own segment to destroy */
		if(shmctl(ipc_packet->resource_id, IPC_RMID, 0))
			perror("shmem::finishIpcShmem(client):shmctl(client)");

		ipc_errno = OK;
		return 0;
	}
}

/* EOF */
