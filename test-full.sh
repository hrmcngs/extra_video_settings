#!/bin/bash
# Build the Forge variant and copy the JAR into the real .minecraft/mods/.
set -e

DIR="$(cd "$(dirname "$0")" && pwd)"

MINECRAFT_MODS="$APPDATA/.minecraft/mods"
# WSL
if [ -d "/mnt/c" ]; then
    MINECRAFT_MODS="/mnt/c/Users/hrmcn/AppData/Roaming/.minecraft/mods"
fi

echo "=== Full Test: extra_video_settings (Forge) ==="
echo "Embeddium + Oculus (実際の Minecraft 環境)"
echo ""

echo "Building Forge JAR..."
"$DIR/buildfo.sh"

JAR="$DIR/forge/forge/build/libs/extra_video_settings-1.1.jar"
if [ ! -f "$JAR" ]; then
    echo "Build failed: JAR not found at $JAR"
    exit 1
fi

cp "$JAR" "$MINECRAFT_MODS/"
echo ""
echo "=== Installed ==="
echo "JAR copied to: $MINECRAFT_MODS/$(basename "$JAR")"
echo ""
echo "Minecraft ランチャーからプレイしてください。"
echo "Embeddium と Oculus が既にインストール済みです。"
