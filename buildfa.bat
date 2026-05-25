@echo off
rem Fabric build. Pass --offline to use only the local Gradle cache.
rem Copies the built remapped JAR to dist\ (skips -dev and -sources jars).
set JAVA_HOME=C:\Program Files\Java\jdk-17
set ROOT=%~dp0
cd /d %ROOT%fabric
call gradlew.bat build %*
if errorlevel 1 exit /b %errorlevel%
if not exist "%ROOT%dist" mkdir "%ROOT%dist"
for %%f in ("build\libs\*.jar") do (
    echo %%~nxf | findstr /R "-dev\.jar$ -sources\.jar$" >nul
    if errorlevel 1 copy /Y "%%f" "%ROOT%dist\" >nul
)
echo Copied -^> %ROOT%dist\
