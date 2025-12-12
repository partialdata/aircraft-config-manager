# Data Model

`ConfigDocument`
- `id` (UUID string) - primary key
- `configId` - provided in JSON
- `aircraftType`
- `softwareVersion`
- `navDataCycle`
- `modules` (comma-separated module names)
- `rawJson` (CLOB) - original payload
- `createdAt` (Instant)

Validation rules (Java): required fields, semver check, AIRAC pattern warning, modules array presence, FMS enabled warning.
Analyzer rules (Python): AIRAC pattern warning, semver error, FMS enabled warning.
