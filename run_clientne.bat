@echo off
rem NeoForge dev client. Pass --offline to use only the local Gradle cache.
set JAVA_HOME=C:\Program Files\Java\jdk-17
cd /d %~dp0forge\neoforge
call gradlew.bat runClient %*
