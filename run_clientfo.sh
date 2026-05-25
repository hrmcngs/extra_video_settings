#!/bin/bash
# Forge dev client. Pass `--offline` to use only the local Gradle cache.
set -e
cd "$(dirname "$0")/forge/forge"
if [ -d "/mnt/c" ]; then
    cmd.exe /c "set JAVA_HOME=C:\\Program Files\\Java\\jdk-17&& gradlew.bat runClient -Dnet.minecraftforge.gradle.check.certs=false $*"
else
    export JAVA_HOME="${JAVA_HOME:-$(/usr/libexec/java_home -v 17 2>/dev/null || echo /usr/lib/jvm/java-17-openjdk)}"
    ./gradlew runClient -Dnet.minecraftforge.gradle.check.certs=false "$@"
fi
