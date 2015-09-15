# Just a makefile

# Compiler and compiler flags
CC=gcc
CFLAGS=-g -Wall -ansi -pedantic -Wmissing-declarations -Wmissing-prototypes -D__EXTENSIONS__ -D_GNU_SOURCE

# A list of executable, object, and library files
EXE=exec_pid fork_pid fork_child fork_exec exit

# Default target
all: $(EXE)
	@echo "All is perfectly done!"

%: %.o
	$(CC) $< -o $@

%.o: %.c
	$(CC) $(CFLAGS) -c $<

# Cleaning up afterwards
clean:
	rm -f $(EXE)
