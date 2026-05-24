@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-17
cd /d %~dp0forge\neoforge
call gradlew.bat build
