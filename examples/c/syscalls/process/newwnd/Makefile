CC=gcc
CFLAGS=-g -Wall -ansi -pedantic -I ../../util/

EXE=cmdwnd cmdwnd-m
OBJ=cmdwnd.o cmdwnd-m.o sysutil.o

all: $(EXE)

$(EXE):	$(OBJ)
	$(CC) sysutil.o cmdwnd.o -o cmdwnd
	$(CC) cmdwnd-m.o -o cmdwnd-m

cmdwnd.o: cmdwnd.c ../../util/sysutil.h
	$(CC) $(CFLAGS) -c cmdwnd.c

cmdwnd-m.o: cmdwnd-m.c
	$(CC) $(CFLAGS) -c cmdwnd-m.c

sysutil.o: ../../util/sysutil.h ../../util/sysutil.c
	$(CC) $(CFLAGS) -c ../../util/sysutil.c


run: all
	./cmdwnd

clean:
	rm -f $(OBJ)

clean-all:
	rm -f $(OBJ) $(EXE)
