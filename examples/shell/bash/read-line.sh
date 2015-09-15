#!/bin/sh

while read line; do
    flaged="${line/\\#include </}"

    if [ "$line" != "$flaged" -a -e "${flaged/>/}" ]; then
        cat "${flaged/>/}"
    else
        echo "$line"
    fi
done
