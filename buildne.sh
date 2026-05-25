#!/bin/bash
# NeoForge build. Pass `--offline` to use only the local Gradle cache.
# Extra args ("$@") are forwarded to gradlew.
# Built JAR is copied to ../../dist/ for easy collection.
set -e
ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT/forge/neoforge"
if [ -d "/mnt/c" ]; then
    cmd.exe /c "set JAVA_HOME=C:\\Program Files\\Java\\jdk-17&& gradlew.bat build $*"
else
    export JAVA_HOME="${JAVA_HOME:-$(/usr/libexec/java_home -v 17 2>/dev/null || echo /usr/lib/jvm/java-17-openjdk)}"
    ./gradlew build "$@"
fi

mkdir -p "$ROOT/dist"
cp -f "$ROOT/forge/neoforge/build/libs/"*.jar "$ROOT/dist/" 2>/dev/null && \
    echo "→ dist/$(ls -1t "$ROOT/forge/neoforge/build/libs/" | head -1)"
