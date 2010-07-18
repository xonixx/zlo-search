#!/bin/sh

source ./mysql_params.sh
source ./mysql_creds_rt.sh

echo Connecting to $mysql_host:$mysql_port with user=$mysql_user
echo Dumping...

"$mysqldump" --triggers --default-character-set=cp1251 --character-sets-dir="$mysql_charsets" \
    -P$mysql_port -h$mysql_host \
    -u$mysql_user -p$mysql_pswd --compact xonix_search | gzip -c > xonix_search.sql.gz

echo Done