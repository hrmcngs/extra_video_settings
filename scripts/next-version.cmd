@echo off
rem Print the NEXT version string (latest CurseForge release + 1).
rem Equivalent of scripts/next-version.sh for Windows.
rem
rem Override: set EVS_VERSION=2.3 to bypass the lookup.
setlocal enabledelayedexpansion

if defined EVS_VERSION (
    echo %EVS_VERSION%
    exit /b 0
)

set "CACHE=%~dp0.last-known-cf-version"
set "FALLBACK=1.1"

rem Fetch via PowerShell — no curl dependency
for /f "delims=" %%v in ('powershell -NoProfile -Command "try { $r = Invoke-RestMethod -Uri 'https://api.cfwidget.com/minecraft/mc-mods/extra-video-settings' -TimeoutSec 10; $vers = @(); foreach ($f in $r.files) { if ($f.name -match '-(\d+(?:\.\d+)+)\.jar') { $vers += ,@($matches[1].Split('.') | ForEach-Object { [int]$_ }) } }; if ($vers) { $best = $vers | Sort-Object -Property @{Expression={ $_[0] }; Descending=$true}, @{Expression={ if ($_.Count -gt 1) { $_[1] } else { 0 } }; Descending=$true}, @{Expression={ if ($_.Count -gt 2) { $_[2] } else { 0 } }; Descending=$true} | Select-Object -First 1; ($best -join '.') } } catch {}"') do set "LATEST=%%v"

if not defined LATEST (
    if exist "%CACHE%" (
        set /p LATEST=<"%CACHE%"
    ) else (
        set "LATEST=%FALLBACK%"
    )
) else (
    >"%CACHE%" echo !LATEST!
)

if "%EVS_NO_BUMP%"=="1" (
    echo !LATEST!
    exit /b 0
)

rem Bump last dot-segment by 1 via PowerShell
for /f "delims=" %%v in ('powershell -NoProfile -Command "$p = '%LATEST%'.Split('.'); $p[-1] = ([int]$p[-1]+1).ToString(); $p -join '.'"') do echo %%v
