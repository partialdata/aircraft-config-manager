#!/usr/bin/env bash
set -euo pipefail

echo "[backend] running tests"
(cd backend && mvn test)

echo "[frontend] running unit tests"
(cd frontend && npm install && npm test)

echo "[analyzer] running FastAPI smoke"
(cd analyzer && python -m py_compile main.py)

echo "All test suites executed."
