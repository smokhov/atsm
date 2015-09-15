# Just a makefile

# Compiler and compiler flags
CC=gcc
CFLAGS=-g -Wall -ansi -pedantic -Wmissing-declarations -Wmissing-prototypes -D__EXTENSIONS__ -D_GNU_SOURCE -I ../../util

# Directory with libraries
LIBDIRS=.

# A list of executable, object, and library files
EXE=shlnno
OBJ=shlnno.o sysutil.o display_line.o
SLIB=libdispl.a
DLIB=libsysutil.so

# Default target
all: $(EXE)
	@echo "All is perfectly done!"

# Other subtargets to make it happen
$(EXE):	shlnno.o $(SLIB) $(DLIB)
	$(CC) shlnno.o -L$(LIBDIRS) -ldispl -L$(LIBDIRS) -lsysutil -o shlnno

shlnno.o: shlnno.c ../../util/sysutil.h display_line.h
	$(CC) $(CFLAGS) -c shlnno.c

$(DLIB): sysutil.o
	ld -shared -o $(DLIB) sysutil.o

$(SLIB): display_line.o
	ar -r $(SLIB) display_line.o

display_line.o: display_line.h display_line.c
	$(CC) $(CFLAGS) -c display_line.c

sysutil.o: ../../util/sysutil.h ../../util/sysutil.c
	$(CC) $(CFLAGS) -c -fPIC ../../util/sysutil.c

run: all
	export LD_LIBRARY_PATH=`pwd`
	ldd ./shlnno
	./shlnno

# Cleaning up afterwards
clean:
	rm -f $(OBJ)

clean-all:
	rm -f $(OBJ) $(EXE) $(SLIB) $(DLIB)
