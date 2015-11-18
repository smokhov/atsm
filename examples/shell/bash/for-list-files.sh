#!/bin/sh

# Note .* wildcard will include files starting with dot, * does not

for file in * .*
do
	echo "$file"
done

# EOF
