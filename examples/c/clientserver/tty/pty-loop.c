/****
 * pty-loop.c
 *
 * Two-process PTY loop.
 * Code is a modified version of loop.c in Stevens, Ch. 19
 *
 * Serguei A. Mokhov
 */

#include <sys/types.h>
#include <sys/socket.h>
#include <signal.h>

/* My Headers */
#include <tty-pty.h>

static void	sig_term(int);
static volatile sig_atomic_t sigcaught; /* set by signal handler */

void
ptyLoop(int ptym, int ignoreeof, int snoopFd, int socket, bool snoop)
{
	pid_t child;
	int   nbytes;
	char  buf[BSIZE];
	FILE* stream; /* to read from file line-by-line */

	if((child = fork()) < 0)
		PERREXIT("ptyLoop():fork() failed")

	else if(child == 0) /* child copies stdin to ptym */
	{
		if(snoop) /* convert fd to stream */
			stream = fdopen(snoopFd, "r");

		for(;;)
		{
			/* Nasty, kludgy cheating */

			/* Get stuff from a socket designed to:
			 *   o either get a command when snooping
			 *   o or as a sync point when replaying from file
			 */
			if((nbytes = recv(socket, buf, BSIZE, 0)) < 0)
				PERREXIT("read error request")

			/* If we're doing playback, buf's contents is read from the snoopfile */
			if(snoop)
			{
				nbytes = strlen(fgets(buf, BSIZE, stream));
/*				if((nbytes = read(STDIN_FILENO, buf, snoopFd)) < 0)
					PERREXIT("read error from stdin")*/
			}

			if(nbytes == 0)
				break; /* EOF on stdin means we're done */

			if(!snoop) /* write to snoopfile when !playback */
				if(writen(snoopFd, buf, strlen(buf)) != nbytes)
					PERREXIT("writen error to snoop fd")

			/* Feed the shell always with whatever junk is in the buf */
			if(writen(ptym, buf, nbytes) != nbytes)
				PERREXIT("writen error to master pty")
		}

		/* We always terminate when we encounter an EOF on stdin,
		   but we only notify the parent if ignoreeof is 0. */
		if(ignoreeof == 0)
			kill(getppid(), SIGTERM);	/* notify parent */

		exit(0);	/* and terminate; child can't return */
	}

	/* parent copies ptym to stdout */
	/*if(signal_intr(SIGTERM, sig_term) == SIG_ERR)
		err_sys("signal_intr error for SIGTERM");*/

	if(signal(SIGTERM, sig_term) == SIG_ERR)
		PERREXIT("signal_intr error for SIGTERM")

	for(;;)
	{
		if((nbytes = read(ptym, buf, BSIZE)) <= 0)
			break;		/* signal caught, error, or EOF */

		if(writen(STDOUT_FILENO, buf, nbytes) != nbytes)
			PERREXIT("writen error to stdout")
	}

	/* There are three ways to get here: sig_term() below caught the
	 * SIGTERM from the child, we read an EOF on the pty master (which
	 * means we have to signal the child to stop), or an error. */

	if(sigcaught == 0) /* tell child if it didn't send us the signal */
		kill(child, SIGTERM);

	return;	/* parent returns to caller */
}


/* The child sends us a SIGTERM when it receives an EOF on
 * the pty slave or encounters a read() error. */

static void
sig_term(int signo)
{
	sigcaught = 1;		/* just set flag and return */
	return;				/* probably interrupts read() of ptym */
}

/* EOF */
