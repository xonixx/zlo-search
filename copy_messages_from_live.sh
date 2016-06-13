#!/usr/bin/env bash

U=xonix
P=xonix_mysql
D=xonix_search
T=zlo_messages
F=/tmp/${T}.sql
PORT=51306

echo "Open tunnel..."
ssh -C -fNL $PORT:127.0.0.1:3306 xonix@37.187.123.144

MAX_ID=$(mysql -u$U -p$P $D -s -N -e "select max(num) from $T")

echo "MAX_ID: $MAX_ID"
echo "Dumping..."
mysqldump --skip-triggers --no-create-info -h127.0.0.1 -P$PORT -u$U -p$P $D $T --where="num>$MAX_ID" > $F

echo "Applying dump..."
cat $F | mysql -u$U -p$P $D

rm $F

echo "Closing tunnel..."
for p in $(ps -ef | grep fNL | grep $PORT | awk '{ print $2 }'); do kill $p; done

echo "Done."