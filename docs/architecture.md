# System Architecture

- **Frontend**: Angular SPA with upload form, table listing stored configs, diff viewer, and report viewer. Built assets served by nginx. API calls proxied to backend.
- **Backend**: Spring Boot REST API exposing upload/list/compare/report. Uses H2 for persistence and RestTemplate to call the analyzer service.
- **Analyzer**: FastAPI service performing secondary validation rules.
- **Networking**: Docker Compose network with service names `frontend`, `backend`, `analyzer`. Backend reaches analyzer at `http://analyzer:8000/analyze`.
- **Data flow**: Upload JSON → Java validation → store in H2 → analyzer HTTP call → respond with merged results. Reports can be generated later by re-running validations.
