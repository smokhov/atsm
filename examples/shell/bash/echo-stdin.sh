#!/bin/bash

#
# Output goes either to STDOUT (1) or a file argument
# if specified
#

theFile=$1

echo "[$theFile]"

if [ $# -eq 0 ]; then
	theFile=1
fi

echo "nnnn" 1>&$theFile

# EOF
