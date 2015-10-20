#!/bin/bash

function showArgs()
{
	echo "Arguments: [$#]"

    for arg in $*
    do
        echo $arg
    done
}


echo "There are $# arguments passed to this shell"
echo "Before shift:"

showArgs $*

echo "After shift:"

shift
showArgs $*

exit 0

# EOF
