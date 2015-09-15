/****
 * pipeline.c
 *
 * Shell's Pipeline Functionality
 *
 * Serguei A. Mokhov
 */

#include <pipeline.h>
#include <microsh.h>
#include <file.h> /* establishRedirection() */

/* Standard headers */

#include <unistd.h> /* syscalls */
#include <sys/wait.h> /* wait() */
#include <stdlib.h> /* exit() */
#include <stdio.h> /* perror() */


extern sh_status_t status;

/**
 * Function: execPipeline()
 * Descritpion: Core of the shell. Executes a pipeline of commands or
 *              a single command recursively.
 * Synopsis: int execPipeline(int p_pipeNum, int p_prevWriteFd)
 * Parameters:
 *  o p_pipeNum - a pipe #, an index to reference commands in
 *    in the cmd_argvs array of sh_status struct. A value of -1 indicates
 *    the end of recursion. Initial value must be a total pipeline size.
 *  o p_prevWriteFd - a file descriptor of a write end of a pipe from the
 *    previous command from the tail of the pipeline. Current child redirects
 *    it's output to this end. Initial value should be -1 when recursion starts.
 * Return value:
 *   o Zero or positive integer means success,
 *   o Negative integer meain failure.
 * Notes: any failures of system calls within the function are reported
 * by perror() function before return.
 */
int
execPipeline(int p_pipeNum, int p_prevWriteFd)
{
	int   rw_fd[2]; /* read and write ends of the current pipe */
	pid_t PID;      /* new child PID                           */

	if(p_pipeNum == -1) /* all children spawned, wait for them to die */
	{
		int i = 0;

		close(p_prevWriteFd); /* close write end of the first command */

		for(; i < status.pipeline_size + 1; i++)
			wait(NULL);

		return 0;
	}

	if(pipe(rw_fd) < 0)
	{
		perror("pipeline.c::execPipline() - pipe() failed");
		return -1;
	}

	if((PID = fork()) < 0)
	{
		perror("pipeline.c::execPipline() - fork() failed");
		return -1;
	}

	else if(PID == 0) /* child */
	{
		dup2(rw_fd[0], STDIN_FILENO); /* we always want to read from stdin */

		if(p_prevWriteFd == -1) /* last cmd - special case */
		{
			if(status.redirection == true)
			{
				int retval =
					status.redirtype ?
						establishRedirection(status.redirect_file, REDIR_OUT_OVERWRITE, STDOUT_FILENO) :
						establishRedirection(status.redirect_file, REDIR_OUT_APPEND, STDOUT_FILENO);

				if(retval < 0) /* Error during establishing redirection */
					exit(1);   /* terminate spawned child               */
			}
		}
		else /* all subsequent cmds */
		{
			dup2(p_prevWriteFd, STDOUT_FILENO); /* Need to stdout to the previous cmd's write end */
			close(p_prevWriteFd);               /* close unneeded file descriptor                 */
		}

		close(rw_fd[1]); /* child closes writing end */

		/* exec() a new program */
		execvp(status.cmd_argvs[p_pipeNum][0], status.cmd_argvs[p_pipeNum]);

		perror("pipeline.c::execPipline() - execvp() failed");
		exit(1); /* this is in the forked child but not loaded program  */
	}
	else /* parent */
	{
		close(rw_fd[0]);                              /* parent closes read end    */
		if(p_prevWriteFd != -1) close(p_prevWriteFd); /* and previous write end    */
		return execPipeline(--p_pipeNum, rw_fd[1]);   /* here's the recursive part */
	}

	return 0; /* should never reach this, but keeps compiler happy */
}

/* EOF */
