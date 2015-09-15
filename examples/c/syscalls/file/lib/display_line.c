#include "display_line.h"
#include "sysutil.h"

#include <unistd.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <stdio.h>

/* Globals */
extern char buf[BSIZE]; 
extern int fd     ;         
extern int arg;             
extern int atLineNo;        
extern int bufLines;        
extern int bufLastLineNo;   
extern int bufFirstLineNo;  
extern int bufCount;        
extern int bBufAligned;     
extern ssize_t nbytes;

/*
 * Displays requested line
 */
void display_line(void)
{
	/* we might not need to read another buffer in */
	/* unless a line spans accross buffers         */

	int bOverBuffer = 0;       /* indicates if a line spans across buffers */
	int iLineLength;           /* length of a line or its part to be displayed */
	static int adjustment = 0; /* adjustment needed if the last line in prev. buff isn't aligned */
	char* line;                /* a line to be written to STDOUT */

	if(!bBufAligned && bufCount != 1) adjustment = 1;
	else  adjustment = 0;

	line = (char*)strdup(locateLine(arg - bufFirstLineNo + 1 + adjustment, buf, &bOverBuffer, &iLineLength));
	write(STDOUT_FILENO, line, iLineLength);
	free(line);

	if(!bOverBuffer && arg == bufLastLineNo) adjustment = 0;

	/* If line spans across buffers */
	while(bOverBuffer)
	{
		bOverBuffer = 0;

		nbytes = read(fd, buf, BSIZE);

		if(nbytes == 0)
		{
			char* msg = (char*)strdup("END OF FILE has been reached!\n");
			write(STDOUT_FILENO, msg, strlen(msg));
			free(msg);
			return;
		}

		bufCount++;

		line = (char*)strdup(locateLine(1, buf, &bOverBuffer, &iLineLength));
		write(STDOUT_FILENO, line, iLineLength);
		free(line);

		adjustment = 1;
		bufFirstLineNo = bufLastLineNo + 1;
		bufLastLineNo = bufFirstLineNo + eolCount(buf) - 1;
	}

	atLineNo = arg;
}
