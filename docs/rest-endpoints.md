# VulnTrack – REST endpoints plan (v1)

All REST APIs go through the API Gateway and are prefixed with `/api/v1`.
This is the plan; the real OpenAPI files will be generated from the code.

## asset-service

| Method and URI | Description | Success | Errors |
|---|---|---|---|
| `POST /api/v1/assets` | Create an asset | 201 | 400 |
| `GET /api/v1/assets` | List assets | 200 | |
| `GET /api/v1/assets/{id}` | Get one asset | 200 | 404 |
| `PUT /api/v1/assets/{id}` | Update an asset | 200 | 400, 404 |
| `DELETE /api/v1/assets/{id}` | Delete an asset | 204 | 404 |
| `POST /api/v1/assets/{id}/scans` | Start a scan on the asset | 202 (`scanId`) | 404, 503 |

## vulnerability-service

| Method and URI | Description | Success | Errors |
|---|---|---|---|
| `POST /api/v1/scan-imports` | Import the findings of a scan (body: `scanId`, `assetId`) | 202 | 400, 503 |
| `GET /api/v1/vulnerabilities` | List and filter (`assetId`, `severity`, `ticketStatus`) | 200 | |
| `GET /api/v1/vulnerabilities/{id}` | Get one vulnerability | 200 | 404 |
| `PATCH /api/v1/vulnerabilities/{id}` | Change status (`OPEN` / `FIXED`) | 200 | 400, 404 |

## report-service

| Method and URI | Description | Success | Errors |
|---|---|---|---|
| `POST /api/v1/reports` | Generate a report (body: `assetId`) | 201 | 400, 503 |
| `GET /api/v1/reports/{id}` | Get one report | 200 | 404 |
| `GET /api/v1/reports` | List reports (`assetId` filter) | 200 | |

## Conventions

- Resources are nouns (`assets`, `scans`, `reports`); no verbs in URIs.
- Inputs are validated (Bean Validation) and rejected with **400**.
- **404** when a resource does not exist, **409** on a duplicate, **503** when a downstream service is unavailable.
- All errors are handled in one `@RestControllerAdvice` and returned in the same JSON shape (Spring `ProblemDetail`).
- Enumerations: `severity` = LOW, MEDIUM, HIGH, CRITICAL; `status` = OPEN, FIXED; `ticketStatus` = NONE, PENDING, CREATED.

## Example

`POST /api/v1/assets`
```json
{ "name": "web-server-01", "type": "SERVER", "address": "10.0.0.12" }
```
`201 Created`
```json
{ "id": "3f2b...", "name": "web-server-01", "type": "SERVER", "address": "10.0.0.12" }
```

`POST /api/v1/assets/{id}/scans` returns `202 Accepted`
```json
{ "scanId": "a91c...", "status": "RUNNING" }
```