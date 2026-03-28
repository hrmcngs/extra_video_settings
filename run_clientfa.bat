@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%
cd /d %~dp0fabric

echo Cleaning caches...
rd /s /q .gradle 2>nul
rd /s /q build 2>nul
rd /s /q "%USERPROFILE%\.gradle\caches\fabric-loom" 2>nul

echo Updating Gradle wrapper...
call .\gradlew.bat wrapper --gradle-version=8.10

echo Building...
call .\gradlew.bat --no-daemon runClient
