#!/bin/bash
# Fabric build. Pass `--offline` to use only the local Gradle cache.
# Extra args ("$@") are forwarded to gradlew.
# Built JAR is copied to ../../dist/ for easy collection.
set -e
ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT/fabric"
if [ -d "/mnt/c" ]; then
    cmd.exe /c "set JAVA_HOME=C:\\Program Files\\Java\\jdk-17&& gradlew.bat build $*"
else
    export JAVA_HOME="${JAVA_HOME:-$(/usr/libexec/java_home -v 17 2>/dev/null || echo /usr/lib/jvm/java-17-openjdk)}"
    ./gradlew build "$@"
fi

mkdir -p "$ROOT/dist"
# Fabric produces both -dev.jar and the final remapped jar; only ship the
# remapped one (which has no -dev suffix and contains the production-mapped
# classes).
for jar in "$ROOT/fabric/build/libs/"*.jar; do
    case "$(basename "$jar")" in
        *-dev.jar|*-sources.jar) continue ;;
    esac
    cp -f "$jar" "$ROOT/dist/"
    echo "→ dist/$(basename "$jar")"
done
