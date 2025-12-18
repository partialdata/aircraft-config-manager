#!/usr/bin/env bash
set -euo pipefail

echo "[backend] building jar"
(cd backend && mvn -q -DskipTests clean package)

echo "[frontend] building Angular"
(cd frontend && npm install && npm run build -- --configuration production)

echo "[analyzer] verifying environment"
PYTHON_BIN="${PYTHON_BIN:-python3}"
if ! command -v "$PYTHON_BIN" >/dev/null 2>&1; then
  PYTHON_BIN="python"
fi
(cd analyzer && "$PYTHON_BIN" -m py_compile main.py)

echo "Artifacts built."
