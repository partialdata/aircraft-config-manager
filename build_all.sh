#!/usr/bin/env bash
set -euo pipefail

echo "[backend] building jar"
(cd backend && mvn -q -DskipTests clean package)

echo "[frontend] building Angular"
(cd frontend && npm install && npm run build -- --configuration production)

echo "[analyzer] verifying environment"
(cd analyzer && python -m py_compile main.py)

echo "Artifacts built."
