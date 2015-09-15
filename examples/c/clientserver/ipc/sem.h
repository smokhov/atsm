/****
 * sem.h
 *
 * Generic Semaphore Manipulation Header
 *
 * Serguei A. Mokhov
 */

#ifndef _SEM_H
#define _SEM_H

#include <sys/types.h> /* key_t */
#include <sys/ipc.h>

/* For the semaphores and stuff */
#define READER 1
#define WRITER 0

/* Standard Semaphore Operations */
int initSemKey(int num_of_sem, key_t key, int* array);
int initSemStr(int num_of_sem, char* str, char proj, int* array);
int V(int sem_id, int sem_num);
int P(int sem_id, int sem_num);
int destroySem(int sem_id);

#endif /* _SEM_H */
