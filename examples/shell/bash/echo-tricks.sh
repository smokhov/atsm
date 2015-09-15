#!/bin/sh

dafile="da-file"

if [ $# -eq 0 ]; then
	dafile=&1
fi

echo "[$dafile]"

sleep 10

echo "nnn"
