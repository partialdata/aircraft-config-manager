# Aircraft Config Manager (ACM)

Local, containerized microservices demo that validates, stores, analyzes, and compares aircraft software configuration JSON. The stack includes an Angular SPA, Java Spring Boot API, and a FastAPI analyzer wired together with Docker Compose.

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
