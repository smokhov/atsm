####
#
# Makefile
#
# Main makefile to compile and run: - Microshell Client 
#
# Deliverable: Lab Assignment #4
# Student:     Serguei A. Mokhov, mokhov@cs.concordia.ca
# ID:          4214374
# Course:      COMP444/4
#
##

CC=gcc
CC_OPTIONS=-Wall -Wmissing-prototypes -Wmissing-declarations -g

COMMON=../common
IPC=../ipc
UTIL=../util
LIBDIRS=-L$(IPC)

SLIB=libipc.a

# Where to look for header files
INC=-I. -I$(COMMON) -I$(IPC) -I../server -I../client -I$(UTIL)

EXE=microsh
OBJ=microsh.o msh_parse.o pipeline.o
UTILOBJ=$(UTIL)/file.o $(UTIL)/msh_string.o

all: $(EXE)
	@echo ""
	@echo "Multi-purpose Microshell Client has been built."
	@echo ""

$(EXE): $(OBJ) $(UTILOBJ) $(IPC)/$(SLIB)
	$(CC) $(OBJ) $(UTILOBJ) $(LIBDIRS) -lipc -o $(EXE)

microsh.o: microsh.h microsh.c $(COMMON)/types.h $(COMMON)/protocol.h $(COMMON)/common.h
	$(CC) $(INC) $(CC_OPTIONS) -c microsh.c

pipeline.o: pipeline.h pipeline.c
	$(CC) $(INC) $(CC_OPTIONS) -c pipeline.c

msh_parse.o: microsh.h msh_parse.h msh_parse.c $(COMMON)/types.h
	$(CC) $(INC) $(CC_OPTIONS) -c msh_parse.c


run:
	make all
	./microsh

log:
	make all
	./microsh | tee microsh.log

debug:
	echo "TODO"

clean:
	rm -f $(EXE) $(OBJ)

clean-all:
	make clean

# EOF
