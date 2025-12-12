# API Endpoints

## POST /api/configs
Upload a configuration. Accepts `multipart/form-data` with either `file` (.json) or `configJson` (string). Returns validation and analyzer results plus stored id.

## GET /api/configs
List stored configuration metadata from H2.

## POST /api/configs/compare
Body: `{ "firstId": "...", "secondId": "..." }`
Returns a structured diff of metadata + module additions/removals.

## GET /api/configs/{id}/report
Returns combined Java validation + analyzer evaluation for a stored config.
