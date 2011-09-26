#!/bin/sh

FIND=/bin/find

$FIND . -name '*.txt' -o -name '*.java' -o -name '*.properties' \
    -o -name '*.sql' -o -name '*.py' -o -name '*.jsp' -o -name '*.js' | \
while read FILE;
do
    echo "Processing: $FILE"
    mv "$FILE" "$FILE.orig" && iconv -f CP1251 -t UTF-8 "$FILE.orig" > "$FILE";
done

echo "---------------------------------------------------"

$FIND -name '*.orig' | while read F;
do
    echo "Removing: $F"
    rm "$F";
done

