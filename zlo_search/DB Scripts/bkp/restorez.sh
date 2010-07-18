#!/bin/sh

source ./mysql_params.sh
source ./mysql_creds_local.sh

echo Connecting to $mysql_host:$mysql_port with user=$mysql_user
echo Restoring...

gzip -d -c xonix_search.sql.gz | "$mysql" --default-character-set=cp1251 --character-sets-dir="$mysql_charsets" \
    -P$mysql_port -u$mysql_user -p$mysql_pswd xonix_search

echo Done

