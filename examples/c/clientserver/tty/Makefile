####
#
# Makefile
#
# Main makefile to build tty/pty stuff
#
# Serguei A. Mokhov, mokhov@cs.concordia.ca
#
##

# Compiler and its options
CC=gcc
#CC_OPTIONS=-Wall -Wmissing-prototypes -Wmissing-declarations -ansi -pedantic -g
CC_OPTIONS=-Wall -Wmissing-prototypes -Wmissing-declarations -g

COMMON=../common
IPC=../ipc

# Where to look for header files and libraries
#INC=-I. -I$(COMMON) -I$(IPC) -I../server -I../client
INC=-I. -I$(COMMON) -I$(IPC)

HEADERS=$(COMMON)/common.h tty-pty.h

# Object files
OBJ=ptyfork.o ptyopen.o pty-loop.o isatty.o ttymodes.o writen.o
SLIB=libtty.a

all: $(SLIB)
	@echo ""
	@echo "TTY/PTY build done."
	@echo ""

$(SLIB): $(OBJ)
	ar -r $(SLIB) $(OBJ)

ptyfork.o: $(HEADERS) ptyfork.c
	$(CC) $(INC) $(CC_OPTIONS) -c ptyfork.c

ptyopen.o: $(HEADERS) ptyopen.c
	$(CC) $(INC) $(CC_OPTIONS) -c ptyopen.c

pty-loop.o: $(HEADERS) pty-loop.c
	$(CC) $(INC) $(CC_OPTIONS) -c pty-loop.c

isatty.o: $(HEADERS) isatty.c
	$(CC) $(INC) $(CC_OPTIONS) -c isatty.c

writen.o: $(HEADERS) writen.c
	$(CC) $(INC) $(CC_OPTIONS) -c writen.c

ttymodes.o: $(HEADERS) ttymodes.c
	$(CC) $(INC) $(CC_OPTIONS) -c ttymodes.c

clean:
	rm -f $(OBJ)

clean-all:
	rm -f $(SLIB) $(OBJ)

# EOF