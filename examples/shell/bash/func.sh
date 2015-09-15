#!/bin/bash

# Calling functions

# Sets argument variables $1 ... $n
# to all the files in a directory (excluding .*)
set - *

function myFunc()
{
	# This is a first argument to a function
	# It will display how many files you've got
	echo $1
}

myFunc $#

exit 0

# EOF
