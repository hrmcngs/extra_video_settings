#!/bin/bash
# Forge build. Pass `--offline` to use only the local Gradle cache.
# Extra args ("$@") are forwarded to gradlew.
# Built JAR is copied to ../../dist/ for easy collection.
#
# Version is auto-bumped from CurseForge (latest published + 1). Override:
#   EVS_VERSION=2.3 ./buildfo.sh    # use literal version
#   EVS_BUMP_FROM=1.5 ./buildfo.sh  # bump from given version
#   EVS_NO_BUMP=1 ./buildfo.sh      # use latest CF version as-is
set -e
ROOT="$(cd "$(dirname "$0")" && pwd)"
EVS_VERSION="${EVS_VERSION:-$("$ROOT/scripts/next-version.sh")}"
echo "→ building version $EVS_VERSION"

cd "$ROOT/forge/forge"
if [ -d "/mnt/c" ]; then
    cmd.exe /c "set JAVA_HOME=C:\\Program Files\\Java\\jdk-17&& gradlew.bat build -Dnet.minecraftforge.gradle.check.certs=false -PevsVersion=$EVS_VERSION $*"
else
    export JAVA_HOME="${JAVA_HOME:-$(/usr/libexec/java_home -v 17 2>/dev/null || echo /usr/lib/jvm/java-17-openjdk)}"
    # cert check is disabled to tolerate DNS-filtering proxies; harmless when
    # the upstream cert is valid (normal HTTPS validation still runs).
    ./gradlew build -Dnet.minecraftforge.gradle.check.certs=false "-PevsVersion=$EVS_VERSION" "$@"
fi

mkdir -p "$ROOT/dist"
SRC="$ROOT/forge/forge/build/libs/extra_video_settings-${EVS_VERSION}.jar"
if [ -f "$SRC" ]; then
    cp -f "$SRC" "$ROOT/dist/"
    echo "→ dist/$(basename "$SRC")"
else
    echo "WARN: expected $SRC not found"
fi
