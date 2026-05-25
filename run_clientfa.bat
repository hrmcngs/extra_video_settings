@echo off
rem Fabric dev client. Pass --offline to use only the local Gradle cache.
set JAVA_HOME=C:\Program Files\Java\jdk-17
cd /d %~dp0fabric
call gradlew.bat runClient %*
