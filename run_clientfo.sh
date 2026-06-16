#!/bin/bash
# Forge dev client. Pass `--offline` to use only the local Gradle cache.
set -e
cd "$(dirname "$0")/forge/forge"

# ForgeGradle 6's MCPRepo / MinecraftRepo cache the Project in a static INSTANCE
# field that the Gradle daemon reuses across builds. If the daemon was first
# started without --offline, that captured state keeps reporting offline=false
# forever, so findVersion() still hits the network. Force a fresh daemon for
# --offline runs.
NEED_FRESH_DAEMON=0
[ "${EVS_OFFLINE:-0}" = "1" ] && NEED_FRESH_DAEMON=1
for arg in "$@"; do
    [ "$arg" = "--offline" ] && NEED_FRESH_DAEMON=1
done

if [ -d "/mnt/c" ]; then
    [ "$NEED_FRESH_DAEMON" = "1" ] && cmd.exe /c "set JAVA_HOME=C:\\Program Files\\Java\\jdk-17&& gradlew.bat --stop" || true
    cmd.exe /c "set JAVA_HOME=C:\\Program Files\\Java\\jdk-17&& gradlew.bat runClient -Dnet.minecraftforge.gradle.check.certs=false $*"
else
    export JAVA_HOME="${JAVA_HOME:-$(/usr/libexec/java_home -v 17 2>/dev/null || echo /usr/lib/jvm/java-17-openjdk)}"
    [ "$NEED_FRESH_DAEMON" = "1" ] && ./gradlew --stop >/dev/null 2>&1 || true
    ./gradlew runClient -Dnet.minecraftforge.gradle.check.certs=false "$@"
fi
