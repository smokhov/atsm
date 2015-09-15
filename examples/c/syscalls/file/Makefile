# Just a makefile

# Compiler and compiler flags
CC=gcc
CFLAGS=-g -Wall -ansi -pedantic -Wmissing-declarations -Wmissing-prototypes -D__EXTENSIONS__ -D_GNU_SOURCE -I ../util/ -L ../util/ 

# A list of executable, object, and library files
EXE=shlnno
#OBJ=shlnno.o sysutil.o
OBJ=shlnno.o

# Default target
all: $(EXE)
	@echo "All is perfectly done!"

# Other subtargets to make it happen
# Presumes sysutils were built first
$(EXE):	$(OBJ) ../util/sysutil.o
	$(CC) $(OBJ) ../util/sysutil.o -o shlnno

%.o: %.c %.h
	$(CC) $(CFLAGS) -c $<

# Cleaning up afterwards
clean:
	rm -f $(OBJ)
