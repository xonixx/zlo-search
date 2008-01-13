@echo off

if "%1"=="d" (
    set J=j_d.bat
    echo Starting in debug...
) else (
    set J=j.bat
    echo Starting normal...
)

title DbDaemon-%SITE_NAME%
call set_env.bat
call %J% org.xonix.zlo.search.daemon.DbDaemon
pause