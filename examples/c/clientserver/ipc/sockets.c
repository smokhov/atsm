/****
 * sockets.c
 *
 * Sockets IPC Implementation
 *
 * Serguei A. Mokhov
 */

/* My Headers*/
#include "sockets.h"

/* Standard Headers*/
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h> /* htons() */
#include <arpa/inet.h>  /* inet_ntoa()*/
#include <stdio.h>      /* perror() */
#include <stdlib.h>     /* exit() */
#include <string.h>     /* memset() */
#include <netdb.h>      /* gethostbyname() */
#include <unistd.h>     /* close() */

extern ipc_error_enum ipc_errno;

int
initIpcSockets(ipc_packet_t* ipc_packet)
{
	struct sockaddr_in srvaddr;
	struct hostent*    hostentry;

	/* Client tries to acquire host's info by its name */
	if(ipc_packet->server == false)
	{
		if((hostentry = gethostbyname((char*)ipc_packet->reqresp)) == NULL)
		{
			perror("sockets::initIpcSockets():gethostbyname() - failed");
			exit(1);
		}
	}

	/* Both server and client allocate socket descriptor */
	if((ipc_packet->resource_id = socket(AF_INET, SOCK_STREAM, 0)) == -1)
	{
		perror("sockets::initIpcSockets():socket() - failed");
		exit(1);
	}

	/* Only server sets the sockets options to reuse socket connections */
	if(ipc_packet->server == true)
	{
		bool bReuse = true;

		if(setsockopt(ipc_packet->resource_id, SOL_SOCKET, SO_REUSEADDR, &bReuse, sizeof(bool)) == -1)
		{
			perror("sockets::initIpcSockets():setsockopt() - failed");
			exit(1);
		}
	}

	/*
	* Both of them need server's address structure:
	* For the server to bind() and to listen() to, and for the
	* client to connect() to.
	*/
	memset(&srvaddr, 0, sizeof(struct sockaddr_in));

	srvaddr.sin_family = AF_INET;            /* Host byte order */
	srvaddr.sin_port   = htons(SERVER_PORT); /* Network byte order (short) */

	/* Server gets arbitrary IP assigned by the OS and client just has to know it */
	if(ipc_packet->server == true)
		srvaddr.sin_addr.s_addr = INADDR_ANY;         /* Let the OS assign IP */
	else
		srvaddr.sin_addr = *((struct in_addr*)hostentry->h_addr);

	/* Server binds to the address and listens */
	if(ipc_packet->server == true)
	{
		if(bind(ipc_packet->resource_id, (struct sockaddr*)&srvaddr, sizeof(struct sockaddr)) == -1)
		{
			perror("sockets::initIpcSockets():bind() - failed");
			exit(1);
		}

		if(listen(ipc_packet->resource_id, BACKLOG) == -1)
		{
			perror("sockets::initIpcSockets():listen() - failed");
			exit(1);
		}
	}

	/* Clients connect(), should run after the server is up */
	else
	{
		if(connect(ipc_packet->resource_id, (struct sockaddr*)&srvaddr, sizeof(struct sockaddr)) == -1)
		{
			perror("sockets::initIpcSockets():connect() - failed");
			exit(1);
		}
	}

	/* Keep a copy of the server address. It's freed in the finishIPC() */
	ipc_packet->extra = malloc(sizeof(struct sockaddr_in));
	memcpy(ipc_packet->extra, &srvaddr, sizeof(struct sockaddr_in));

	return ipc_errno = OK;
}


int
sendRequestSockets(ipc_packet_t* ipc_packet)
{
	if(ipc_packet->server) /* server may not send requests in this implementation */
	{
		ipc_errno = ERR_SRV_REQUEST;
		return -1;
	}
	else
	{
		int nbytes; /* number of bytes sent */

		nbytes = send(ipc_packet->resource_id, ipc_packet->reqresp, sizeof(request_t), 0);

		/* TODO: fix ipcerror handling */
		if(nbytes == -1)
			perror("sockets.c::sendRequestSockets():send() - failed");
		else
			ipc_errno = OK;

		return nbytes;
	}
}


int
sendResponseSockets(ipc_packet_t* ipc_packet)
{
	if(ipc_packet->server == false) /* client may not send responses in this implementation */
	{
		ipc_errno = ERR_CLI_RESPONSE;
		return -1;
	}
	else
	{
		int nbytes; /* number of bytes sent */

		nbytes = send(ipc_packet->resource_id, ipc_packet->reqresp, sizeof(response_t), 0);

		/* TODO: fix ipcerror handling */
		if(nbytes == -1)
			perror("sockets.c::sendResponseSockets():send() - failed");
		else
			ipc_errno = OK;

		return nbytes;
	}
}


int
receiveRequestSockets(ipc_packet_t* ipc_packet)
{
	if(ipc_packet->server == false) /* client may not receive requests in this implementation */
	{
		ipc_errno = ERR_CLI_REQUEST;
		return -1;
	}
	else
	{
		int       nbytes; /* number of bytes received */
		int       newSockFd;
		socklen_t addr_in_size = sizeof(struct sockaddr_in);

		printf("Accepting on socket with descriptor %d.\n", ipc_packet->resource_id);
		fflush(stdout);

		if((newSockFd = accept(ipc_packet->resource_id, (struct sockaddr *)ipc_packet->extra, &addr_in_size)) == -1)
		{
			perror("sockets.c::receiveRequestSockets():accept() - failed");
			return -1;
		}

		printf
		(
			"Got connection from %s\n",
			inet_ntoa(((struct sockaddr_in*)ipc_packet->extra)->sin_addr)
		);
		fflush(stdout);

/*		nbytes = recv(ipc_packet->resource_id, ipc_packet->reqresp, sizeof(request_t), 0);
*/
		nbytes = recv(newSockFd, ipc_packet->reqresp, sizeof(request_t), 0);

		/* TODO: fix ipcerror handling */
		if(nbytes == -1)
			perror("sockets.c::sendResponseSockets():recv() - failed");
		else
			ipc_errno = OK;

		return newSockFd;
	}
}


int
receiveResponseSockets(ipc_packet_t* ipc_packet)
{
	if(ipc_packet->server) /* server may not receive responses in this implementation */
	{
		ipc_errno = ERR_SRV_RESPONSE;
		return -1;
	}
	else
	{
		int nbytes; /* number of bytes received */

		nbytes = recv(ipc_packet->resource_id, ipc_packet->reqresp, sizeof(response_t), 0);

		/* TODO: fix ipcerror handling */
		if(nbytes == -1)
			perror("sockets.c::sendResponseSockets():send() - failed");
		else
			ipc_errno = OK;

		return nbytes;
	}
}


int
finishIpcSockets(ipc_packet_t* ipc_packet)
{
	free(ipc_packet->extra);
	return close(ipc_packet->resource_id);
}

/* EOF */
