#!/bin/sh

#declare TOMCAT

. _cfg.sh
. _lib.sh

echo "rm logs..."
rm -rf $TOMCAT/logs/*

echo "rm work..."
rm -rf $TOMCAT/work/*

echo "rm ROOT folder..."
rm -rf $TOMCAT/webapps/ROOT

WA="$TOMCAT/webapps"
ROOTWAR="$WA/ROOT.war"

if [ -f $ROOTWAR ]; then
    echo "bkp ROOT.war..."
    mv $ROOTWAR "$WA/ROOT.war_$(now)"
else
    echo "$ROOTWAR not present."
fi

echo "Done."

