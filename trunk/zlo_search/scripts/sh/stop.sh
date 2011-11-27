#!/bin/sh

#declare TOMCAT

. _cfg.sh

echo "Pkill..."
pkill java
sleep 10

echo "Shutdown..."
$TOMCAT/bin/shutdown.sh
sleep 10
