#!/bin/bash
set -e

echo "=== Test Play: extra_video_settings ==="
echo "Mods: Embeddium 0.3.31 + Oculus 1.8.0"
echo ""

# Oculus を run/mods/ に配置（fg.deobf経由だとMixin refmapが壊れるため）
MODS_DIR="run/mods"
OCULUS_JAR="$MODS_DIR/oculus-1.8.0.jar"
OCULUS_URL="https://www.cursemaven.com/curse/maven/oculus-581495/6020952/oculus-581495-6020952.jar"

mkdir -p "$MODS_DIR"
if [ ! -f "$OCULUS_JAR" ]; then
    echo "Downloading Oculus 1.8.0..."
    curl -L -o "$OCULUS_JAR" "$OCULUS_URL"
    echo "Done."
fi

if [ -d "/mnt/c" ]; then
    cmd.exe /c "set JAVA_HOME=C:\Program Files\Java\jdk-17&& gradlew.bat runClient"
else
    export JAVA_HOME="${JAVA_HOME:-C:/Program Files/Java/jdk-17}"
    ./gradlew runClient
fi
