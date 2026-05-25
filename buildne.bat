@echo off
rem NeoForge build. Pass --offline to use only the local Gradle cache.
rem Auto-bumps version from CurseForge (latest + 1).
setlocal enabledelayedexpansion

set JAVA_HOME=C:\Program Files\Java\jdk-17
set ROOT=%~dp0

if not defined EVS_VERSION (
    for /f "delims=" %%v in ('call "%ROOT%scripts\next-version.cmd"') do set "EVS_VERSION=%%v"
)
echo -^> building version !EVS_VERSION!

cd /d %ROOT%forge\neoforge
call gradlew.bat build "-PevsVersion=!EVS_VERSION!" %*
if errorlevel 1 exit /b %errorlevel%
if not exist "%ROOT%dist" mkdir "%ROOT%dist"
copy /Y "build\libs\*.jar" "%ROOT%dist\" >nul
echo Copied -^> %ROOT%dist\
