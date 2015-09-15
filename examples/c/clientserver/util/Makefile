####
#
# Makefile
#
# A makefile to build utility modules
#
# Serguei A. Mokhov, mokhov@cs.concordia.ca
#
##

# Compiler and its options
CC=gcc
DEBUG=-g -D_DEBUG
#ISO=-ansi -pedantic
CC_OPTIONS=-Wall -Wmissing-prototypes -Wmissing-declarations $(ISO) $(DEBUG)

COMMON=../common

# Where to look for header files and libraries
#INC=-I. -I$(COMMON) -I$(IPC) -I../server -I../client -I$(UTIL)
INC=-I. -I$(COMMON)
#LIBDIR=$(IPC)

# Executables and object files
#EXE=mtimesrv snoopsrv
OBJ=file.o msh_string.o
#LIB=libipc.so
#LIB=libipc.a

all: $(OBJ)
	@echo ""
	@echo "All utility modules are built."
	@echo ""

file.o: file.h file.c $(COMMON)/types.h $(COMMON)/common.h
	$(CC) $(INC) $(CC_OPTIONS) -c file.c

msh_string.o: $(COMMON)/common.h msh_string.h msh_string.c $(COMMON)/types.h
	$(CC) $(INC) $(CC_OPTIONS) -c msh_string.c

clean:
	rm -f $(OBJ)

clean-all:
	make clean

# EOF