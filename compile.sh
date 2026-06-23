#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAVAFX_SDK="$("${PROJECT_DIR}/scripts/javafx-home.sh")"
OUT_DIR="${PROJECT_DIR}/out"
SOURCE_LIST="$(mktemp)"

trap 'rm -f "${SOURCE_LIST}"' EXIT

rm -rf "${OUT_DIR}"
mkdir -p "${OUT_DIR}"
find "${PROJECT_DIR}/src" -name '*.java' | sort > "${SOURCE_LIST}"

javac \
  --module-path "${JAVAFX_SDK}/lib" \
  --add-modules javafx.controls,javafx.media \
  -d "${OUT_DIR}" \
  @"${SOURCE_LIST}"

echo "Compiled Tank Battle Arena into ${OUT_DIR}"
