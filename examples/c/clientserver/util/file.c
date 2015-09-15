#include <file.h>

/* Standard Headers */
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>  /* fprintf */
#include <unistd.h> /* dup */

/**
 * Function: establishRedirection()
 * Description: sets up redirection of STDOUT to file.
 * Synopsis: int establishRedirection(void)
 * Return value:
 *   o Zero or positive integer means success,
 *   o Negative integer means failure.
 * Notes: any failures of system calls within the function are reported
 * by perror() function before return.
 */
int
establishRedirection(const char* filename, redir_type_enum redir_type, int std_io_files)
{
	int retval = 0; /* return value: < 0 means error */
	int redirFd;    /* a file desc. to redirect to   */

	switch(redir_type)
	{
		case REDIR_OUT_OVERWRITE: /* > - overwrite */
			redirFd = open(filename, O_CREAT | O_WRONLY, S_IRUSR | S_IWUSR);
			break;

		case REDIR_OUT_APPEND: /* >> - append */
			redirFd = open(filename, O_CREAT | O_WRONLY | O_APPEND, S_IRUSR | S_IWUSR);
			break;

		default:
			fprintf
			(
				stderr,
				"Invalid or unsupported redirection type (%d) requestested.\n",
				redir_type
			);
			return -1;
	}

	if((retval = redirFd) == -1)
		perror("file::establishRedirection() - open() failed");
	else
	{
		switch(std_io_files)
		{
			case STDIN_FILENO:
				retval = close(STDIN_FILENO);
				break;

			case STDOUT_FILENO:
				retval = close(STDOUT_FILENO);
				break;

			case STDERR_FILENO:
				retval = close(STDERR_FILENO);
				break;

			case STDOUT_FILENO | STDERR_FILENO:
				retval  = close(STDOUT_FILENO);
				retval *= close(STDERR_FILENO);
				retval *= dup(redirFd);
				break;

			default:
				fprintf
				(
					stderr,
					"Invalid or unsupported standard I/O file type(s) (%d) requestested.\n",
					std_io_files
				);
				return -1;
		}

		if(retval < 0)
			perror("file::establishRedirection() - failed");
		else
		{
			retval = dup(redirFd);

			if(retval < 0)
				perror("file::establishRedirection() - dup() failed");

			retval = close(redirFd);

			if(retval < 0)
				perror("file::establishRedirection() - close(redirFd) failed");
		}
	}

	return retval;
}

/* EOF */
