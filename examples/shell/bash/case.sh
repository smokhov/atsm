#!/bin/sh

#
# Analog to tcsh's switch-case
#

case $# in

    1) cat >> $1
	   ;;

    2) cat >>$2 <$1
	   ;;

    3) case $3 in
           -[abc]) echo "-a -b or -c"
				   ;;

        -foo|-bar) echo "-foo or -bar"
				   ;;
       esac
	   ;;

    *) echo "we accept up to 3 args only."; exit 127
	   ;;
esac

# EOF
