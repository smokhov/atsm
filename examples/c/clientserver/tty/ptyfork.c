/****
 * ptyfork.c
 *
 * Forks a PTY child and connects them to them master and slave ends.
 * Code modified from Stevens
 *
 * Serguei A. Mokhov
 */

#include	<sys/types.h>
#include	<termios.h>

#ifndef	TIOCGWINSZ
#	include <sys/ioctl.h>	/* 44BSD requires this too */
#endif

/* My Headers */
#include <types.h>
#include <tty-pty.h>

pid_t
pty_fork
(
	int* ptrfdm,
	char* slave_name,
	const struct termios* slave_termios,
	const struct winsize* slave_winsize
)
{
	int   fdm, fds;
	pid_t pid;
	char  pts_name[PTSNAME_SIZE];

	if((fdm = ptym_open(pts_name)) < 0)
	{
		fprintf(stderr, "can't open master pty: %s", pts_name);
		perror("");
		exit(1);
	}

	if(slave_name != NULL)
		strcpy(slave_name, pts_name); /* return name of slave */

	if((pid = fork()) < 0)
		return(-1);

	else if(pid == 0)		/* child */
	{
		if(setsid() < 0)
		{
			perror("setsid error");
			exit(1);
		}

		/* SVR4 acquires controlling terminal on open() */
		if((fds = ptys_open(fdm, pts_name)) < 0)
		{
			perror("can't open slave pty");
			exit(1);
		}

		close(fdm);		/* all done with master in child */

#if	defined(TIOCSCTTY) && !defined(CIBAUD)
		/* 44BSD way to acquire controlling terminal */
		/* !CIBAUD to avoid doing this under SunOS */
		if(ioctl(fds, TIOCSCTTY, NULLPTR) < 0)
		{
			perror("TIOCSCTTY error");
			exit(1);
		}
#endif

		/* set slave's termios and window size */
		if(slave_termios != NULL)
		{
			if(tcsetattr(fds, TCSANOW, slave_termios) < 0)
			{
				perror("tcsetattr error on slave pty");
				exit(1);
			}
		}

		if(slave_winsize != NULL)
		{
			if(ioctl(fds, TIOCSWINSZ, slave_winsize) < 0)
			{
				perror("TIOCSWINSZ error on slave pty");
				exit(1);
			}
		}

		/* slave becomes stdin/stdout/stderr of child */
		if(dup2(fds, STDIN_FILENO) != STDIN_FILENO)
		{
			perror("dup2 error to stdin");
			exit(1);
		}

		if(dup2(fds, STDOUT_FILENO) != STDOUT_FILENO)
		{
			perror("dup2 error to stdout");
			exit(1);
		}

		if(dup2(fds, STDERR_FILENO) != STDERR_FILENO)
		{
			perror("dup2 error to stderr");
			exit(1);
		}

		if(fds > STDERR_FILENO)
			close(fds);

		return(0);		/* child returns 0 just like fork() */
	}

	else /* parent */
	{
		*ptrfdm = fdm;	/* return fd of master */
		return(pid);	/* parent returns pid of child */
	}
}

/* EOF */
