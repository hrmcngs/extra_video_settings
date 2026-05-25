#!/bin/bash
# Forge build. Pass `--offline` to use only the local Gradle cache.
# Extra args ("$@") are forwarded to gradlew.
# Built JAR is copied to ../../dist/ for easy collection.
set -e
ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT/forge/forge"
if [ -d "/mnt/c" ]; then
    cmd.exe /c "set JAVA_HOME=C:\\Program Files\\Java\\jdk-17&& gradlew.bat build -Dnet.minecraftforge.gradle.check.certs=false $*"
else
    export JAVA_HOME="${JAVA_HOME:-$(/usr/libexec/java_home -v 17 2>/dev/null || echo /usr/lib/jvm/java-17-openjdk)}"
    # cert check is disabled to tolerate DNS-filtering proxies; harmless when
    # the upstream cert is valid (normal HTTPS validation still runs).
    ./gradlew build -Dnet.minecraftforge.gradle.check.certs=false "$@"
fi

mkdir -p "$ROOT/dist"
cp -f "$ROOT/forge/forge/build/libs/"*.jar "$ROOT/dist/" 2>/dev/null && \
    echo "→ dist/$(ls -1t "$ROOT/forge/forge/build/libs/" | head -1)"
