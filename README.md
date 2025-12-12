# Aircraft Config Manager (ACM)

Local, containerized microservices demo that validates, stores, analyzes, and compares aircraft software configuration JSON. The stack includes an Angular SPA, Java Spring Boot API, and a FastAPI analyzer wired together with Docker Compose.

## What problem does this solve?
- Aircraft software configs need to be validated before loading onto a jet; mistakes can ground an aircraft or require costly rework.
- Ops/avionics teams often juggle multiple tools to sanity-check JSON configs, run deeper rules, and compare versions—this project bundles those steps into one UI + API.
- The demo shows a realistic internal workflow: upload a config, run fast Java validations, call out to a Python rule engine, store the record, and generate reports/diffs for reviewers.

## Architecture
```
+-----------------+       +------------------+       +-------------------+
| Angular SPA     | <---> | Spring Boot API  | <---> | FastAPI Analyzer  |
| (nginx)         |       | + H2 database    |       | rule checks       |
+-----------------+       +------------------+       +-------------------+
```

- Frontend: Angular 17 built and served by nginx (port 4200)
- Backend: Spring Boot REST API with H2 in-memory storage (port 8080)
- Analyzer: FastAPI rule service (port 8000)
- Orchestration: Docker Compose

## Quickstart (Docker)
1. Ensure Docker is running.
2. From the repo root run:
   ```bash
   ./start_local.sh
   ```
3. Open the UI at http://localhost:4200.

## Using the Web UI
- **Upload a config**: In the “Upload configuration” card, either choose a `.json` file or paste JSON into the textbox, then click Submit. On success you’ll see the stored ID plus validation/analyzer counts.

- **Use the samples**: In `sample-configs/`, `sample-a.json` and `sample-b.json` are ready to upload. Upload both to generate two entries you can compare.

- **View stored configs**: The table lists each stored config with metadata. Use buttons `A` and `B` to select two entries for comparison, then click **Compare** to see added/removed modules and field changes.

- **View report**: Click **Report** on any row to open a combined Java + Analyzer report showing metadata, Java validation warnings/errors, and analyzer warnings/errors.

- **Expected behavior with samples**:
  - `sample-a.json`: Valid semver and AIRAC; should show no errors, maybe a warning if you remove FMS.
  - `sample-b.json`: Slightly different nav cycle/version/modules; after uploading both A and B, the compare view will show B’s additions/removals.

## Local builds (optional)
- Build all artifacts locally:
  ```bash
  ./build_all.sh
  ```
- Run all tests:
  ```bash
  ./run_tests.sh
  ```

## Key Endpoints (backend)
- `POST /api/configs` (multipart): upload config via `file` or `configJson`
- `GET /api/configs`: list stored config metadata
- `POST /api/configs/compare`: diff two configs `{ firstId, secondId }`
- `GET /api/configs/{id}/report`: combined Java + Python validation report

## Sample data
Sample JSON payloads live in `sample-configs/` for quick uploads.

## Development notes
- Analyzer URL is configurable via `analyzer.url` property or `ANALYZER_URL` env var.
- H2 is in-memory by default; switch to file-backed by changing `application.yml`.
- Frontend proxies API calls to the backend container via nginx config.
