@echo off

set J=j.bat

title OptimizeIndex

echo Stopping services...
net stop ZSDaemons
net stop ZSTomcat
sleep 10

call set_env.bat
call %J% info.xonix.zlo.search.progs.OptimizeAllIndexes

echo Starting services...
net start ZSDaemons
net start ZSTomcat
pause