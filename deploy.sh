#!/bin/bash

SERV=xonix@rt.mipt.ru
TOMCAT=/usr/local/apache-tomcat-7.0/
WAR=$(ls -v zlo_web/target/*.war | tail -n 1)
BKP=$(date +%y%m%d%H%M)

WA=$TOMCAT/webapps

ssh $SERV "
echo 'Stop...'

$TOMCAT/bin/shutdown.sh; sleep 10; killall -9 java

echo 'Clean/Backup...'

rm -rf $WA/ROOT
mv $WA/ROOT.war $WA/ROOT.war_$BKP

rm -rf $TOMCAT/work/*
"

scp $WAR $SERV:$WA/ROOT.war

ssh $SERV "
echo 'Starting...'

CATALINA_OPTS='-server -Xmx300m -XX:MaxPermSize=256m -XX:PermSize=128m'
export CATALINA_OPTS

$TOMCAT/bin/startup.sh;

tail -f $TOMCAT/logs/catalina.out
"
