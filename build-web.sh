#!/usr/bin/env bash
# Builds the browser version of the game into build/web/ (index.html + game.jar).
# Used by the GitHub Pages workflow; you can also run it locally:  bash build-web.sh
set -euo pipefail
cd "$(dirname "$0")"

rm -rf build
mkdir -p build/classes build/web

# Compile for Java 8 (CheerpJ's default in-browser runtime). module-info.java is Java 9+ only, so skip it.
find src -name "*.java" ! -name "module-info.java" > build/sources.txt
javac --release 8 -encoding UTF-8 -nowarn -d build/classes @build/sources.txt

# Maps, sprites and the font go in the jar root, exactly like res/ on the desktop classpath.
cp -R res/. build/classes/
find build/classes -name "*.java" -delete

# the jar tool: on PATH (GitHub's build machine), else from JAVA_HOME
JAR="jar"
if ! command -v jar >/dev/null 2>&1 && [ -n "${JAVA_HOME:-}" ]; then JAR="$JAVA_HOME/bin/jar"; fi
"$JAR" --create --file build/web/game.jar --main-class main.MainGame -C build/classes .
cp web/index.html build/web/
echo "Built build/web/game.jar and build/web/index.html"
