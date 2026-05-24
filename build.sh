#!/bin/bash
# Build all three loader variants (Forge, NeoForge, Fabric).
# Use the per-variant scripts directly if you only want one.
set -e

DIR="$(cd "$(dirname "$0")" && pwd)"

echo "=== Building Forge ==="
"$DIR/buildfo.sh"

echo ""
echo "=== Building NeoForge ==="
"$DIR/buildne.sh"

echo ""
echo "=== Building Fabric ==="
"$DIR/buildfa.sh"

echo ""
echo "=== All builds complete ==="
echo "JARs:"
ls -1 "$DIR/forge/forge/build/libs/"*.jar 2>/dev/null || echo "  (forge build/libs missing)"
ls -1 "$DIR/forge/neoforge/build/libs/"*.jar 2>/dev/null || echo "  (neoforge build/libs missing)"
ls -1 "$DIR/fabric/build/libs/"*.jar 2>/dev/null || echo "  (fabric build/libs missing)"
