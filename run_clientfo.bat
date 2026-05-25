@echo off
rem Forge dev client. Pass --offline to use only the local Gradle cache.
set JAVA_HOME=C:\Program Files\Java\jdk-17
cd /d %~dp0forge\forge
call gradlew.bat runClient -Dnet.minecraftforge.gradle.check.certs=false %*
