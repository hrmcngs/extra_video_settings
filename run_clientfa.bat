@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%
cd fabric
call gradlew.bat --stop 2>nul
rd /s /q .gradle 2>nul
rd /s /q build 2>nul
call gradlew.bat runClient
