####
#
# Makefile
#
# Main makefile to build: - ASCII character device driver
#                         - main() to test the driver
#
# Serguei Mokhov
#
##

CC=gcc
DEBUG=-g -D_DEBUG
DEFINE=-DMODULE -D__KERNEL__ -DLINUX
WARNINGS=-Wall -Wmissing-prototypes -Wmissing-declarations
#ISO=-ansi -pedantic
CC_OPTIONS=-O1 $(WARNINGS) $(ISO) $(DEBUG) $(DEFINE)

# Where to look for header files
INC=-I. -I/usr/include -I/usr/src/kernels/`uname -r`/include

DRIVER=ascii.o
MODULE=ascii.ko
EXE=ascii-test
OBJ=main.o $(DRIVER)

obj-m += $(DRIVER)

all: $(EXE)
	make -C /lib/modules/$(shell uname -r)/build M=$(PWD) modules
	@echo ""
	@echo "ASCII Character Device Driver and the Test Program have been built."
	@echo "Type 'make register' to register ASCII device module."
	@echo "Then follow module's suggestion to mknod /dev/CSI230ASCII with the correct major number!"
	@echo "EXTREME CAUTION IS HIGHLY RECOMMENDED. NOT TO USE ON YOUR OWN"
	@echo "MACHINE OR NUCLEAR POWER PLANTS :) THERE MAY BE CONSEQUENCES"
	@echo ""

clean:
	rm -f $(EXE) $(OBJ)
	make -C /lib/modules/$(shell uname -r)/build M=$(PWD) clean

compile: $(EXE) $(OBJ)

register: $(DRIVER)
	insmod ./$(MODULE)
	modinfo $(MODULE)
	lsmod | grep ascii
	@echo ""
	@echo "ASCII Character Device Driver has been built and registered."
	@echo ""

$(EXE): main.o
	$(CC) main.o -o $(EXE)

main.o: main.c common.h
	$(CC) $(CC_OPTIONS) $(INC) -c main.c

$(DRIVER): types.h ascii.h ascii.c
	$(CC) $(CC_OPTIONS) $(INC) -c ascii.c

# https://access.redhat.com/documentation/en-US/Red_Hat_Enterprise_Linux/7/html/Kernel_Administration_Guide/sect-signing-kernel-modules-for-secure-boot.html
el-sign-tools:
	yum -y install openssl kernel-devel perl mokutil keyutils

clean-all:
	make clean
	rmmod ascii
	lsmod

# EOF
