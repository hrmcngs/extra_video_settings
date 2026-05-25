#!/bin/bash
# Build all three loader variants (Forge, NeoForge, Fabric).
# Forwards any extra args to each loader's Gradle invocation, so
# `./build.sh --offline` runs all three in offline mode.
#
# Version is computed ONCE here (via scripts/next-version.sh) and the same
# value is passed to all three sub-builds so the dist/ jars share a version.
# Per-variant scripts also call next-version.sh when run individually.
set -e

DIR="$(cd "$(dirname "$0")" && pwd)"

# Convenience: EVS_OFFLINE=1 ./build.sh -> implies --offline
if [ "${EVS_OFFLINE:-0}" = "1" ]; then
    set -- --offline "$@"
fi

# Compute once, export, so sub-scripts skip their own lookup.
export EVS_VERSION="${EVS_VERSION:-$("$DIR/scripts/next-version.sh")}"
echo "=== Building version $EVS_VERSION ==="

# Wipe stale jars so dist/ ends with just this version's artifacts.
rm -f "$DIR/dist/extra_video_settings"*.jar 2>/dev/null || true

echo ""
echo "=== Forge ==="
"$DIR/buildfo.sh" "$@"

echo ""
echo "=== NeoForge ==="
"$DIR/buildne.sh" "$@"

echo ""
echo "=== Fabric ==="
"$DIR/buildfa.sh" "$@"

echo ""
echo "=== All builds complete (version $EVS_VERSION) ==="
echo "dist/:"
if [ -d "$DIR/dist" ]; then
    ls -1lh "$DIR/dist/" | tail -n +2
else
    echo "  (dist/ missing)"
fi
