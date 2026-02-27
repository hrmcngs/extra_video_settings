#!/bin/bash
set -e

echo "=== Test Play: extra_video_settings (dev) ==="
echo "Mods: Embeddium 0.3.31"
echo "(Oculus は開発環境非対応のため test-full.sh を使用)"
echo ""

# Oculus が run/mods/ に残っていると落ちるので除外
if [ -f "run/mods/oculus-1.8.0.jar" ]; then
    mv "run/mods/oculus-1.8.0.jar" "run/mods/oculus-1.8.0.jar.disabled"
fi

if [ -d "/mnt/c" ]; then
    cmd.exe /c "set JAVA_HOME=C:\Program Files\Java\jdk-17&& gradlew.bat runClient"
else
    export JAVA_HOME="${JAVA_HOME:-C:/Program Files/Java/jdk-17}"
    ./gradlew runClient
fi
