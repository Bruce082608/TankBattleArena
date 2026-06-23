#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAVAFX_SDK="$("${PROJECT_DIR}/scripts/javafx-home.sh")"

if [[ ! -d "${PROJECT_DIR}/out" ]]; then
  "${PROJECT_DIR}/compile.sh"
fi

cd "${PROJECT_DIR}"
java \
  --module-path "${JAVAFX_SDK}/lib:${PROJECT_DIR}/out" \
  --module tank.battle.arena/app.Main
