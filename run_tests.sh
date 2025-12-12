#!/usr/bin/env bash
set -euo pipefail

echo "[backend] running tests"
(cd backend && mvn test)

echo "[frontend] running unit tests"
(cd frontend && npm install && npm test)

echo "[analyzer] running FastAPI tests"
(cd analyzer && pip install -q -r requirements.txt && pytest -q)

echo "All test suites executed."
