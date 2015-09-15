####
#
# Makefile
#
# A makefile to compile IPC-related stuff
#
# Serguei A. Mokhov
#
##

# Compiler and its options
CC=gcc
DEBUG=-g -D_DEBUG
#ISO=-ansi -pedantic
CC_OPTIONS=-Wall -Wmissing-prototypes -Wmissing-declarations $(ISO) $(DEBUG)
#CC_COMPILE=-c -fPIC
CC_COMPILE=-c

COMMON=../common
IPC=.

# Where to look for include files
INC=-I$(COMMON) -I$(IPC) -I../server -I../client
DLIB=libipc.so
SLIB=libipc.a

OBJ=ipc.o shmem.o sockets.o npipes.o sem.o msq.o

all: $(SLIB) #$(DLIB)
	@echo ""
	@echo "IPC Library has been built."
	@echo ""

$(SLIB): $(OBJ)
	ar -r $(SLIB) $(OBJ)

#$(DLIB): $(OBJ)
#	ld -shared -o $(DLIB) $(OBJ)
#	LD_LIBRARY_PATH=$(LD_LIBRARY_PATH):$(IPC); export LD_LIBRARY_PATH

ipc.o: ipc.h ipc.c $(COMMON)/protocol.h shmem.o sockets.o npipes.o msq.o
	$(CC) $(INC) $(CC_OPTIONS) $(CC_COMPILE) ipc.c

sem.o: sem.h sem.c $(COMMON)/types.h $(COMMON)/common.h
	$(CC) $(INC) $(CC_OPTIONS) $(CC_COMPILE) sem.c

shmem.o: shmem.h shmem.c $(COMMON)/protocol.h ipc.h
	$(CC) $(INC) $(CC_OPTIONS) $(CC_COMPILE) shmem.c

sockets.o: sockets.h sockets.c $(COMMON)/protocol.h ipc.h
	$(CC) $(INC) $(CC_OPTIONS) $(CC_COMPILE) sockets.c

npipes.o: npipes.h npipes.c $(COMMON)/protocol.h ipc.h
	$(CC) $(INC) $(CC_OPTIONS) $(CC_COMPILE) npipes.c

msq.o: msq.h msq.c $(COMMON)/protocol.h msq.h
	$(CC) $(INC) $(CC_OPTIONS) $(CC_COMPILE) msq.c


clean:
	rm -f $(OBJ)

clean-all:
	rm -f $(OBJ) $(SLIB) $(DLIB)

# EOF
