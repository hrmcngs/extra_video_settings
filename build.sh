#!/bin/bash
set -e

echo "=== Building extra_video_settings ==="

if [ -d "/mnt/c" ]; then
    # WSL: use cmd.exe to run gradlew.bat with Windows Java
    cmd.exe /c "set JAVA_HOME=C:\Program Files\Java\jdk-17&& gradlew.bat build"
else
    export JAVA_HOME="${JAVA_HOME:-C:/Program Files/Java/jdk-17}"
    ./gradlew build
fi

JAR_PATH="build/libs/extra_video_settings-1.0.jar"
if [ -f "$JAR_PATH" ]; then
    echo ""
    echo "=== Build successful ==="
    echo "JAR: $JAR_PATH"
else
    echo "Build failed: JAR not found"
    exit 1
fi
