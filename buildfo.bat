@echo off
rem Forge build. Pass --offline to use only the local Gradle cache.
rem Copies the built JAR to dist\ at the end.
set JAVA_HOME=C:\Program Files\Java\jdk-17
set ROOT=%~dp0
cd /d %ROOT%forge\forge
call gradlew.bat build -Dnet.minecraftforge.gradle.check.certs=false %*
if errorlevel 1 exit /b %errorlevel%
if not exist "%ROOT%dist" mkdir "%ROOT%dist"
copy /Y "build\libs\*.jar" "%ROOT%dist\" >nul
echo Copied -^> %ROOT%dist\
