#!/bin/bash
# Print the NEXT version string for the mod, computed as:
#   (latest version published on CurseForge) + bump last segment by 1
#
# Source: https://api.cfwidget.com/minecraft/mc-mods/extra-video-settings
# (cfwidget is a third-party JSON wrapper over the CurseForge web API; no
# CurseForge API key required.)
#
# Override:
#   EVS_VERSION=2.3       — bypass the lookup and use a literal version
#   EVS_BUMP_FROM=1.5     — bypass the lookup and bump from this version
#   EVS_NO_BUMP=1         — print the latest CurseForge version as-is (no +1)
#
# Fallback chain (if cfwidget is unreachable):
#   1. scripts/.last-known-cf-version (cached previous successful lookup)
#   2. 1.1 (the value the mod shipped at the time this script was added)
#
# Output: single line, e.g. "1.2"
set -e

DIR="$(cd "$(dirname "$0")" && pwd)"
CACHE="$DIR/.last-known-cf-version"
HARDCODED_FALLBACK="1.1"

bump() {
    # Increment the last dot-segment of a semver-like string by 1
    python3 -c "
v = '$1'.split('.')
v[-1] = str(int(v[-1]) + 1)
print('.'.join(v))
"
}

# 1. Explicit override
if [ -n "${EVS_VERSION:-}" ]; then
    echo "$EVS_VERSION"
    exit 0
fi

# 2. Explicit bump-from override
if [ -n "${EVS_BUMP_FROM:-}" ]; then
    bump "$EVS_BUMP_FROM"
    exit 0
fi

# 3. Fetch from cfwidget
LATEST=$(curl -fsS -m 10 "https://api.cfwidget.com/minecraft/mc-mods/extra-video-settings" 2>/dev/null \
    | python3 -c "
import sys, json, re
try:
    d = json.load(sys.stdin)
    versions = []
    for f in d.get('files', []):
        m = re.search(r'-(\d+(?:\.\d+)+)\.jar', f.get('name', ''))
        if m:
            versions.append(tuple(int(x) for x in m.group(1).split('.')))
    if versions:
        latest = max(versions)
        print('.'.join(str(x) for x in latest))
except Exception:
    pass
" 2>/dev/null || true)

# 4. Cache or fallback
if [ -n "$LATEST" ]; then
    echo "$LATEST" > "$CACHE"
elif [ -f "$CACHE" ]; then
    LATEST=$(cat "$CACHE")
else
    LATEST="$HARDCODED_FALLBACK"
fi

if [ "${EVS_NO_BUMP:-0}" = "1" ]; then
    echo "$LATEST"
else
    bump "$LATEST"
fi
