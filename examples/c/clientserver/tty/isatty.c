/****
 * isatty.c
 *
 * Checks if a given file descriptor is a PTY.
 * Code is from Stevens
 *
 * Serguei A. Mokhov, mokhov@cs.concordia.ca
 */

#include	<termios.h>

/* My Headers */
#include <tty-pty.h>

int
isatty(int fd)
{
	struct termios	term;

	return(tcgetattr(fd, &term) != -1); /* true if no error (is a tty) */
}

/* EOF */
