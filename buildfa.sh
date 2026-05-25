#!/bin/bash
# Fabric build. Pass `--offline` to use only the local Gradle cache.
# Extra args ("$@") are forwarded to gradlew.
# Built JAR is written directly to ../dist/ by the build.gradle's build hook.
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

# Filename pattern matches base.archivesName in fabric/build.gradle.
DIST=""
for jar in "$ROOT/dist/extra_video_settings-fabric-"*"-${EVS_VERSION}.jar"; do
    [ -f "$jar" ] && DIST="$jar"
done
if [ -n "$DIST" ]; then
    echo "→ $DIST"
else
    echo "WARN: no matching dist jar for version $EVS_VERSION"
fi
