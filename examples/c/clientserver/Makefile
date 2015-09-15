####
#
# Makefile
#
# Global makefile to compile and run both
# The Microshell Client and The Snoop Server
#
# To use with GNU make.
#
# Serguei A. Mokhov
#   $Id: Makefile,v 1.2 2014/11/11 21:35:40 mokhov Exp $
#
##

EXE=snoopsrv rshell rsnoop

all: 
	gmake -C ipc $@
	gmake -C tty $@
	gmake -C util $@
	gmake -C server $@
	gmake -C client $@
	ln -s -f server/snoopsrv snoopsrv
	ln -s -f client/microsh rshell
	ln -s -f client/microsh rsnoop
	@echo ""
	@echo "All Done:"
	@echo ""
	@ls -l --color | grep lrwx
	@echo ""

run:
	gmake all
	./snoopsrv
	sleep 1 # to give a lot of time for the server to set up public stuff
	xterm -e ./rshell --sockets
#	xterm -e ./microsh --shmem
#	xterm -e ./microsh --npipes
#	xterm -e ./microsh --msgq

clean:
	rm -f $(EXE) mtimesrv.log snoopsrv.log snoopfile*
	gmake -C ipc $@
	gmake -C tty $@
	gmake -C util $@
	gmake -C server $@
	gmake -C client $@
	@echo ""
	@echo "Cleaning Done"
	@echo ""

clean-all:
	rm -f $(EXE) mtimesrv.log snoopsrv.log snoopfile*
	gmake -C ipc $@
	gmake -C tty $@
	gmake -C util $@
	gmake -C server $@
	gmake -C client $@
	@echo ""
	@echo "Cleaning All Done"
	@echo ""

# EOF
