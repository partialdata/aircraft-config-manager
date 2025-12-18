#!/usr/bin/env bash
set -euo pipefail

echo "[backend] running tests"
(cd backend && mvn test)

echo "[frontend] running unit tests"
(cd frontend && npm install && npm test)

echo "[analyzer] running FastAPI tests"
PYTHON_BIN="${PYTHON_BIN:-python3.10}"
if ! command -v "$PYTHON_BIN" >/dev/null 2>&1; then
  PYTHON_BIN="python3"
fi
(
  cd analyzer
  VENV_DIR=".venv"
  if [ ! -d "$VENV_DIR" ]; then
    "$PYTHON_BIN" -m venv "$VENV_DIR"
  fi
  VENV_PY="$VENV_DIR/bin/python"
  "$VENV_PY" -m pip install -q -r requirements.txt
  "$VENV_PY" -m pytest -q
)

echo "All test suites executed."
