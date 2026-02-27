#!/bin/bash
set -e

MINECRAFT_MODS="$APPDATA/.minecraft/mods"
# WSL
if [ -d "/mnt/c" ]; then
    MINECRAFT_MODS="/mnt/c/Users/hrmcn/AppData/Roaming/.minecraft/mods"
fi

echo "=== Full Test: extra_video_settings ==="
echo "Embeddium + Oculus (実際の Minecraft 環境)"
echo ""

# ビルド
echo "Building JAR..."
if [ -d "/mnt/c" ]; then
    cmd.exe /c "set JAVA_HOME=C:\Program Files\Java\jdk-17&& gradlew.bat build" 2>&1
else
    export JAVA_HOME="${JAVA_HOME:-C:/Program Files/Java/jdk-17}"
    ./gradlew build
fi

JAR="build/libs/extra_video_settings-1.0.jar"
if [ ! -f "$JAR" ]; then
    echo "Build failed: JAR not found"
    exit 1
fi

# .minecraft/mods/ にコピー
cp "$JAR" "$MINECRAFT_MODS/"
echo ""
echo "=== Installed ==="
echo "JAR copied to: $MINECRAFT_MODS/extra_video_settings-1.0.jar"
echo ""
echo "Minecraft ランチャーからプレイしてください。"
echo "Embeddium と Oculus が既にインストール済みです。"
