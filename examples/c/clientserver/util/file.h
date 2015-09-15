#ifndef _FILE_H
#define _FILE_H

typedef enum
{
	REDIR_OUT_OVERWRITE = 0, /* >  */
	REDIR_OUT_APPEND    = 1  /* >> */
} redir_type_enum;

int establishRedirection(const char* filename, redir_type_enum, int std_io_files);

#endif /* _FILE_H */
