@echo off
rem Fabric build. Pass --offline to use only the local Gradle cache.
rem Auto-bumps version from CurseForge (latest + 1).
setlocal enabledelayedexpansion

set JAVA_HOME=C:\Program Files\Java\jdk-17
set ROOT=%~dp0

if not defined EVS_VERSION (
    for /f "delims=" %%v in ('call "%ROOT%scripts\next-version.cmd"') do set "EVS_VERSION=%%v"
)
echo -^> building version !EVS_VERSION!

cd /d %ROOT%fabric
call gradlew.bat build "-PevsVersion=!EVS_VERSION!" %*
if errorlevel 1 exit /b %errorlevel%
if not exist "%ROOT%dist" mkdir "%ROOT%dist"
for %%f in ("build\libs\*.jar") do (
    echo %%~nxf | findstr /R "-dev\.jar$ -sources\.jar$" >nul
    if errorlevel 1 copy /Y "%%f" "%ROOT%dist\" >nul
)
echo Copied -^> %ROOT%dist\
