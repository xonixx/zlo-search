@echo off
title Indexer
call set_env.bat
call j.bat org.xonix.zlo.search.daemon.IndexerDaemon
pause