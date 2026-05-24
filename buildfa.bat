@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-17
cd /d %~dp0fabric
call gradlew.bat build
