# Just a makefile

# Compiler and compiler flags
CC=gcc
CFLAGS=-g -Wall -ansi -pedantic
#CFLAGS=-g -Wall -ansi -pedantic -Wmissing-declarations -Wmissing-prototypes -D__EXTENSIONS__ -D_GNU_SOURCE

# A list of executable, object, and library files
EXE=FindPrimes
OBJ=Main.o CheckPrime.o

# Default target
all: $(EXE)
	@echo "All is perfectly done!"

$(EXE): $(OBJ)
	$(CC) $(OBJ) -o $(EXE)

%.o: %.c Defs.h Externs.h
	$(CC) $(CFLAGS) -c $<

# Cleaning up afterwards
clean:
	rm -f $(OBJ) $(EXE)
