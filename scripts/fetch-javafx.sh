#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
VERSION="${JAVAFX_VERSION:-17.0.16}"

case "$(uname -s)" in
  Darwin) OS_NAME="osx" ;;
  Linux) OS_NAME="linux" ;;
  MINGW*|MSYS*|CYGWIN*) OS_NAME="windows" ;;
  *) echo "Unsupported OS. Set JAVAFX_HOME manually." >&2; exit 1 ;;
esac

case "$(uname -m)" in
  arm64|aarch64) ARCH_NAME="aarch64" ;;
  x86_64|amd64) ARCH_NAME="x64" ;;
  *) echo "Unsupported architecture. Set JAVAFX_HOME manually." >&2; exit 1 ;;
esac

ARCHIVE="openjfx-${VERSION}_${OS_NAME}-${ARCH_NAME}_bin-sdk.zip"
URL="https://download2.gluonhq.com/openjfx/${VERSION}/${ARCHIVE}"
DEST_DIR="${PROJECT_DIR}/vendor"
DEST_ZIP="${DEST_DIR}/${ARCHIVE}"

mkdir -p "${DEST_DIR}"
echo "Downloading ${URL}"
curl -L "${URL}" -o "${DEST_ZIP}"
echo "Extracting ${ARCHIVE}"
unzip -q -o "${DEST_ZIP}" -d "${DEST_DIR}"
echo "JavaFX SDK ready at ${DEST_DIR}/javafx-sdk-${VERSION}"
