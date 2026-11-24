# Time log

| Date | Minutes | Work done |
|---|---:|---|
| 28 Oct | 30min | Repository structure, .gitignore, .env.example, ticket.xsd |
| 29 Oct | 1h | scan.proto, REST endpoints plan, GitHub board |
| 30 Oct | 1h | asset-service skeleton, Dockerfile, Compose, subject sheet |
| 30 Oct | 1h | ticketing-service skeleton (US-04, U1), application.yml, Actuator health check, GitHub PR |
| 30 Oct | 10min | Migration asset-service vers Spring Boot 4.1.2, compilation et tests rÃ©ussis |
| 1 Nov | 30min | VulnTrack README documentation and GitHub repository setup |
| 2 Nov | 1h | US-04: creation of ticketing XSD-generated Java classes, including request/response classes, enums and fault detail |
| 2---4 Nov | 2h | US-04: implementation of SOAP endpoint, WebServiceConfig, WSDL publication and createTicket/getTicketStatus SOAP requests |
| 4---7 Nov |  3h30min| US-05: implementation of getTicketStatus operation, TicketService, TicketNotFoundException and database persistence with JPA |
| 7 Nov |2h | US-06: SOAP Fault handling for InvalidSeverity and TicketNotFound, including validation of error responses |
| 7----17 Nov |3h| US-07: Dockerfile and Docker Compose configuration for ticketing-service |
| 17----18 Nov | 1h30min  | US-27: creation of unit tests and SOAP integration tests for ticketing-service |
| 18 Nov | 2h | US-27: validation of successful CRITICAL ticket creation, InvalidSeverity fault and TicketNotFound fault |
| 20 Nov | 1h | Correction of ticketing-service/.dockerignore; verification that data/ and target/ are ignored by Git |
| 21 Nov | 1h | Rebuild of ticketing-service Docker image and container health check; service successfully reached healthy status |
|21 Nov |1h30min | US-07 â€” Scan Service Skeleton
- Created the `scan-service` Spring Boot 4.1.2 microservice skeleton.
- Configured the HTTP server (port 8084) and gRPC server (port 9090).
- Added `scan.proto` and configured Protobuf code generation with Maven.
- Implemented a minimal `StartScan` gRPC method.
- Configured H2 database, JPA and Spring Boot Actuator health endpoints.
- Added `.dockerignore` and Git ignore rules for local database files.
- Validated successful Maven compilation and application startup.
- Tested HTTP health, liveness and readiness endpoints.
- Created and merged PR: `feature/US-07-scan-service-skeleton`.
- Commit: `86835ef`.
- Status: Completed.
| 22 Nov | 3h30min | scan-service: Scan entity, simulator, ScanService, gRPC RPCs + statuses, manual grpcurl checks |
| 22 Nov | 3h30 | scan-service: Scan entity, finding simulator, ScanService, gRPC RPCs with INVALID_ARGUMENT and NOT_FOUND, manual grpcurl checks (US-07, US-08, US-09) |
| 23 Nov | 3h20 + grpcurl install | scan-service: unit tests, gRPC integration test, Dockerfile, Compose entry, persistence check after restart; grpcurl installed and Windows quoting fixed (US-25, US-27, US-28) |
| 24 Nov | 2h30 | Sprint 1 review and retro, README skeleton, time log rebuilt, Spring Boot and calendar aligned in the specification, tag v0.1.0, Sprint 2 board ready |
