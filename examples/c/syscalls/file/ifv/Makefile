CC=gcc
CFLAGS=-pipe \
	-g \
	-ansi \
	-pedantic \
	-Wall \
	-Wmissing-declarations \
	-Wmissing-prototypes \
	-D__EXTENSIONS__ \
	-D_GNU_SOURCE \
	-I ../../util

EXE=ifv
OBJ=sysutil.o ifv.o

all: $(EXE)

$(EXE):	$(OBJ)
	$(CC) $(OBJ) -o ifv

ifv.o: ifv.c ../../util/sysutil.h
	$(CC) $(CFLAGS) -c ifv.c

sysutil.o: ../../util/sysutil.h ../../util/sysutil.c
	$(CC) $(CFLAGS) -c ../../util/sysutil.c

run: all
	./ifv ifv.c

clean:
	rm -f $(OBJ)

clean-all:
	rm -f $(OBJ) $(EXE)
