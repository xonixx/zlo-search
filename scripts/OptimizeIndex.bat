@echo off

set J=j.bat

title OptimizeIndex
call set_env.bat
call %J% org.xonix.zlo.search.progs.OptimizeIndex
pause