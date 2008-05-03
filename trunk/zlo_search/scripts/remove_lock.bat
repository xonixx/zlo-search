@echo off
call set_env.bat
if exist %LI%\write.lock (
	echo Write lock exists, removing...
	del %LI%\write.lock
	echo Done.
) else (
	echo There is no lock...
)
pause