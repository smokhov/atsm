#!/bin/bash

#
# For tchs replace 'bash' with 'tcsh' in the above
#

#
# To redirect this out to an HTML file:
# ./here-doc.sh > file.html
#

# This illustrates on how to use Here Document,
# a document, raw character data, embedded within
# a script. You can put almost anything in there, and
# it won't be interpreted by the shell, but instead
# passed to the STDOUT directly.

cat << DOC_NAME
<html>
<head>
	<title>Hi!</title>
</head>

<body>
	My current dir is: $PWD
	<h1>Bye!</h1>
</body>

</html>
DOC_NAME

# EOF
