/****
 * sem.c
 *
 * Generic Semaphore Manipulation Module
 *
 * Serguei A. Mokhov
 */

/* My headers */
#include <sem.h>
#include <types.h> /* semun */
#include <common.h> /* #defines */

/* Standard headers */
#include <stdio.h>
#include <stdlib.h>
#include <sys/sem.h>
#include <string.h>
#include <errno.h>


/*
 * Semaphore init routine using KEY
 */
int
initSemKey(int num_of_sem, key_t key, int* array)
{
	int         sem_id; /* semaphore set ID */
	union semun sem_un; /* semun for semctl() */
	int         retval; /* retval from other system calls */

	if(num_of_sem <= 0)
	{
		printf("sem::initSemKey() - Number of semaphores cannot be less than zero or zero. Supplied: %d\n", num_of_sem);
		return -1;
	}

	sem_id = semget(key, num_of_sem, 0600 | IPC_CREAT);

	if(sem_id == -1)
	{
		perror("sem::initSemKey():semget() - failed");
		exit(1);
	}

	if(array != (int*)0)
		sem_un.array = (unsigned short int*)array;

	retval = semctl(sem_id, 0, SETALL, sem_un);

	if(retval == -1)
	{
		perror("sem::initSemKey():semctl() - failed");
		exit(1);
	}

	return sem_id;
}


/*
 * Semaohore init routine using filename string and a project letter
 */
int
initSemStr(int num_of_sem, char* str, char proj, int* array)
{
	key_t key = ftok(str, proj);

	if(key == -1)
	{
		perror("sem::initSemStr():ftok() - failed");
		exit(1);
	}

	return initSemKey(num_of_sem, key, array);
}


/*
 * A typical V operation
 */
int
V(int sem_id, int sem_num)
{
	struct sembuf sem_buf;
	int           retval;

    sem_buf.sem_num = sem_num; /* perform op on the sem_num semaphore */
    sem_buf.sem_op  = 1;       /* V-operation */
    sem_buf.sem_flg = 0;       /* some dummy flags down here */

    if((retval = semop(sem_id, &sem_buf, 1)) == -1)
	{
		if(errno == EIDRM)
			printf("sem::V() - OOPS! Semaphore was destroyed at another end!\n") ;
		else if(errno == EINTR)
			printf("sem::V() - was interrupted to catch a signal.\n");
	    else
		{
			perror("sem::V():semop() - failed");
			exit(1);
		}
	}

	return retval;
}


/*
 * A typical P operation
 */
int
P(int sem_id, int sem_num)
{
	struct sembuf sem_buf;
	int           retval;

    sem_buf.sem_num = sem_num; /* perform op on the sem_num semaphore */
    sem_buf.sem_op  = -1;      /* P-operation */
    sem_buf.sem_flg = 0;       /* some dummy flags down here */

    if((retval = semop(sem_id, &sem_buf, 1)) == -1)
	{
		if(errno == EIDRM)
			printf("sem::P() - OOPS! Semaphore was destroyed on another end!\n") ;
		else if(errno == EINTR)
			printf("sem::P() - was interrupted to catch a signal.\n");
	    else
		{
			perror("sem::P():semop() - failed");
			exit(1);
		}
	}

	return retval;
}


/*
 * Semaphore clean up routine
 */
int
destroySem(int sem_id)
{
	int retval = semctl(sem_id, 0, IPC_RMID, 0);

	if(retval == -1)
	{
		perror("sem::destroySem():semctl() - failed");
		exit(1);
	}

	return retval;
}

/* EOF */
