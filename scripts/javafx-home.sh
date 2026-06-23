#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

if [[ -n "${JAVAFX_HOME:-}" && -d "${JAVAFX_HOME}/lib" ]]; then
  echo "${JAVAFX_HOME}"
  exit 0
fi

for candidate in \
  "${PROJECT_DIR}/vendor/javafx-sdk-17.0.16" \
  "${HOME}/Downloads/javafx-sdk-17.0.16" \
  "${HOME}/Downloads/javafx-sdk-21.0.8" \
  "${HOME}/Downloads/javafx-sdk-21.0.7" \
  "${HOME}/Downloads/javafx-sdk-21.0.6"; do
  if [[ -d "${candidate}/lib" ]]; then
    echo "${candidate}"
    exit 0
  fi
done

cat >&2 <<'MESSAGE'
No compatible JavaFX SDK was found.

Run:
  ./scripts/fetch-javafx.sh

or set JAVAFX_HOME manually:
  JAVAFX_HOME=/path/to/javafx-sdk ./compile.sh
MESSAGE
exit 1
