#!/bin/bash
# Run the Forge dev client (with Embeddium loaded automatically).
# For NeoForge or Fabric, use run_clientne.sh / run_clientfa.sh.
set -e

DIR="$(cd "$(dirname "$0")" && pwd)"

echo "=== Test Play: extra_video_settings — Forge (dev) ==="
echo "Mods: Embeddium 0.3.31"
echo "(Oculus は開発環境非対応のため test-full.sh を使用)"
echo ""

# Oculus が run/mods/ に残っていると落ちるので除外
if [ -f "$DIR/forge/forge/run/mods/oculus-1.8.0.jar" ]; then
    mv "$DIR/forge/forge/run/mods/oculus-1.8.0.jar" "$DIR/forge/forge/run/mods/oculus-1.8.0.jar.disabled"
fi

exec "$DIR/run_clientfo.sh"
