#!/bin/sh

# Will list all files in a dir using 'while' loop
# 'test', and 'shift' commands

# set $1 to the first file in the current directory
# set $2 to the next, and so on

set - *

while test $1
do
	echo $1
	shift
done

# EOF
