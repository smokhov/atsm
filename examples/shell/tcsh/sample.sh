#!/bin/tcsh

# The above shebang is saying that this
# script must be interpreted by a C-Shell

if( $# < 1 ) then
	echo "Sorry, a filename is required..."
	echo ""
	echo "usage: sample.sh filename"
	exit
endif 

if( ! -e $1 ) then
	echo File $1 does not exits!
	exit
endif

if( ! -f $1 ) then
	echo File $1 is not an ordianry file!
	exit
endif

options:

echo "Please enter an option and its value..."
echo "Available options: case <u, l>, <enter> for nothing"
echo -n "Option:"

set valid_options = (case)
set valid_values = (l u)

set option = ($<)

if( $#option == 0 ) then 
    goto options
endif

while($option[1] != $valid_options[1])
	echo "Invalid option: $option[1]"
	echo -n "Option: "
	set option = ($<)
end

while(($option[2] != $valid_values[1]) && ($option[2] != $valid_values[2]))
	echo "Invalid option value: $option[2]"
	goto options
end

if($option[2] == "u") then
    cat $1 | tr "a-z" "A-Z"
else
    cat $1 | tr "A-Z" "a-z"
endif


