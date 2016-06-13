#!/bin/bash

SERV=xonix@37.187.123.144
TOMCAT=/home/xonix/apache-tomcat/
WAR=$(ls -v zlo_web/target/*.war | tail -n 1)
BKP=$(date +%y%m%d%H%M)

WA=$TOMCAT/webapps

echo "Building..."
mvn clean package -DskipTests

ssh $SERV "
echo 'Stop...'

$TOMCAT/bin/shutdown.sh; sleep 10; killall -9 java

echo 'Clean/Backup...'

rm -rf $WA/ROOT

if [ -f $WA/ROOT.war ]
then
    mv $WA/ROOT.war $WA/ROOT.war_$BKP
fi

rm -rf $TOMCAT/work/*
"

echo "Uploading..."
scp $WAR $SERV:$WA/ROOT.war

ssh $SERV "
echo 'Starting...'

$TOMCAT/bin/startup.sh;

tail -f $TOMCAT/logs/catalina.out
"
