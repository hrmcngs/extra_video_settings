#!/bin/bash
# Fabric build. Pass `--offline` to use only the local Gradle cache.
# Extra args ("$@") are forwarded to gradlew.
# Built JAR is copied to ../../dist/ for easy collection.
#
# Version is auto-bumped from CurseForge (latest published + 1). Override:
#   EVS_VERSION=2.3 ./buildfa.sh    # use literal version
#   EVS_BUMP_FROM=1.5 ./buildfa.sh  # bump from given version
#   EVS_NO_BUMP=1 ./buildfa.sh      # use latest CF version as-is
set -e
ROOT="$(cd "$(dirname "$0")" && pwd)"
EVS_VERSION="${EVS_VERSION:-$("$ROOT/scripts/next-version.sh")}"
echo "→ building version $EVS_VERSION"

cd "$ROOT/fabric"
if [ -d "/mnt/c" ]; then
    cmd.exe /c "set JAVA_HOME=C:\\Program Files\\Java\\jdk-17&& gradlew.bat build -PevsVersion=$EVS_VERSION $*"
else
    export JAVA_HOME="${JAVA_HOME:-$(/usr/libexec/java_home -v 17 2>/dev/null || echo /usr/lib/jvm/java-17-openjdk)}"
    ./gradlew build "-PevsVersion=$EVS_VERSION" "$@"
fi

mkdir -p "$ROOT/dist"
# Fabric's archivesName includes the minecraft version, so the filename
# pattern is extra_video_settings-fabric-<mc>-<evs>.jar. Pick the one
# matching the current EVS_VERSION and skip -dev / -sources.
SRC=""
for jar in "$ROOT/fabric/build/libs/"*"-${EVS_VERSION}.jar"; do
    case "$(basename "$jar")" in
        *-dev.jar|*-sources.jar) continue ;;
    esac
    [ -f "$jar" ] && SRC="$jar"
done
if [ -n "$SRC" ]; then
    cp -f "$SRC" "$ROOT/dist/"
    echo "→ dist/$(basename "$SRC")"
else
    echo "WARN: no matching jar for version $EVS_VERSION"
fi
