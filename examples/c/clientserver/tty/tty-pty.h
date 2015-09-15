/****
 * tty-pty.c
 *
 * Two-process PTY loop.
 * Code is a modified version of ourhdr.h of Stevens as well as my own
 *
 * Serguei A. Mokhov
 */

#ifndef _TTY_PTY_H
#define _TTY_PTY_H

#include	<sys/types.h>	/* required for some of our prototypes */
#include	<string.h>		/* for convenience */
#include	<unistd.h>		/* for convenience */

#include <common.h>

#define PTSNAME_SIZE 20

int		 tty_cbreak(int);			/* {Prog raw} */
int		 tty_raw(int);				/* {Prog raw} */
int		 tty_reset(int);			/* {Prog raw} */
void	 tty_atexit(void);			/* {Prog raw} */

#ifdef	ECHO	/* only if <termios.h> has been included */
struct termios	*tty_termios(void);	/* {Prog raw} */
#endif

int		 ptym_open(char *);			/* {Progs ptyopen_svr4 ptyopen_44bsd} */
int		 ptys_open(int, char *);	/* {Progs ptyopen_svr4 ptyopen_44bsd} */
#ifdef	TIOCGWINSZ
pid_t	 pty_fork(int *, char *, const struct termios *,
				  const struct winsize *);	/* {Prog ptyfork} */
#endif

#include <types.h> /* bool */

void ptyLoop(int ptym, int ignoreeof, int snoopFd, int socket, bool snoop);
int isatty(int);

ssize_t writen(int fd, const void *vptr, size_t n);

#endif /* _TTY_PTY_H */
