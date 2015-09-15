####
#
# Makefile
#
# Main makefile to build: - The Micro Time Server
#                         - The Snoop Server
#
# Serguei A. Mokhov
#
##

# Compiler and its options
CC=gcc
DEBUG=-g #-D_DEBUG
#LOG=-D_PRINT -D_LOG -D_ELOG -D_OLOG
#ISO=-ansi -pedantic
CC_OPTIONS=-Wall -Wmissing-prototypes -Wmissing-declarations $(ISO) $(DEBUG)

COMMON=../common
IPC=../ipc
UTIL=../util
TTY=../tty

# Where to look for header files and libraries
INC=-I. -I$(COMMON) -I$(IPC) -I../server -I../client -I$(UTIL) -I$(TTY)
LIBDIRS=-L$(IPC) -L$(TTY)

# Executables and object files
EXE=mtimesrv snoopsrv
OBJ=server.o mtimesrv.o snoopsrv.o
LIBIPC=libipc.a
LIBTTY=libtty.a

all: $(EXE)
	@echo ""
	@echo "All servers are built."
	@echo ""

mtimesrv: server.o mtimesrv.o $(IPC)/$(IPCLIB) $(UTIL)/file.o
	$(CC) server.o mtimesrv.o $(UTIL)/file.o $(LIBDIRS) -lipc -o mtimesrv

snoopsrv: server.o snoopsrv.o $(IPC)/$(IPCLIB) $(TTY)/$(TTYLIB) $(UTIL)/file.o
	$(CC) server.o snoopsrv.o $(UTIL)/file.o $(LIBDIRS) -lipc -ltty -o snoopsrv

server.o: server.h server.c $(COMMON)/types.h $(COMMON)/protocol.h $(COMMON)/common.h
	$(CC) $(INC) $(CC_OPTIONS) -c server.c

mtimesrv.o: mtimesrv.h mtimesrv.c server.h $(UTIL)/file.h
	$(CC) $(INC) $(CC_OPTIONS) -c mtimesrv.c

snoopsrv.o: snoopsrv.h snoopsrv.c server.h $(UTIL)/file.h
	$(CC) $(INC) $(CC_OPTIONS) -c snoopsrv.c

run:
	@make all
	@tcsh -c 'echo -n "Which one do you want to run? (mtimesrv, snoopsrv - default, both) : "; set srv="$<"; if($srv == "") set $srv="snoopsrv"; ./$srv;'

log:
	@echo "TODO: make log"

debug:
	@echo "TODO: make debug"

clean:
	rm -f $(EXE) $(OBJ) mtimesrv.log snoopsrv.log

clean-all:
	make clean

# EOF