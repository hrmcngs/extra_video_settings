#!/bin/bash
# Build all three loader variants (Forge, NeoForge, Fabric).
# Forwards any extra args to each loader's Gradle invocation, so
# `./build.sh --offline` runs all three in offline mode.
# Each per-variant script copies its JAR into dist/; the final listing
# is printed at the end.
set -e

DIR="$(cd "$(dirname "$0")" && pwd)"

# Convenience: EVS_OFFLINE=1 ./build.sh -> implies --offline
if [ "${EVS_OFFLINE:-0}" = "1" ]; then
    set -- --offline "$@"
fi

echo "=== Building Forge ==="
"$DIR/buildfo.sh" "$@"

echo ""
echo "=== Building NeoForge ==="
"$DIR/buildne.sh" "$@"

echo ""
echo "=== Building Fabric ==="
"$DIR/buildfa.sh" "$@"

echo ""
echo "=== All builds complete ==="
echo "dist/:"
if [ -d "$DIR/dist" ]; then
    ls -1lh "$DIR/dist/" | tail -n +2
else
    echo "  (dist/ missing)"
fi
