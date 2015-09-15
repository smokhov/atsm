#!/bin/sh

# A variation of set-shift.sh

set - * .*

while [ $# -gt 0 ];
do
	echo $1
	shift
done

# EOF
