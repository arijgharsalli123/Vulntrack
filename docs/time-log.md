# Time log

| Date | Minutes | Work done |
|---|---:|---|
| 28 Oct | 30min | Repository structure, .gitignore, .env.example, ticket.xsd |
| 29 Oct | 1h | scan.proto, REST endpoints plan, GitHub board |
| 30 Oct | 1h | asset-service skeleton, Dockerfile, Compose, subject sheet |
| 30 Oct | 1h | ticketing-service skeleton (US-04, U1), application.yml, Actuator health check, GitHub PR |
| 30 Oct | 10min | Migration asset-service vers Spring Boot 4.1.2, compilation et tests réussis |
| 1 Nov | 30min | VulnTrack README documentation and GitHub repository setup |
| 2 Nov | 1h | US-04: creation of ticketing XSD-generated Java classes, including request/response classes, enums and fault detail |
| 2–4 Nov | 2h | US-04: implementation of SOAP endpoint, WebServiceConfig, WSDL publication and createTicket/getTicketStatus SOAP requests |
| 4–7 Nov |  3h30min| US-05: implementation of getTicketStatus operation, TicketService, TicketNotFoundException and database persistence with JPA |
| 7 Nov | À compléter | US-06: SOAP Fault handling for InvalidSeverity and TicketNotFound, including validation of error responses |
| 7–17 Nov | À compléter | US-07: Dockerfile and Docker Compose configuration for ticketing-service |
| 17–18 Nov | À compléter | US-27: creation of unit tests and SOAP integration tests for ticketing-service |
| 18 Nov | À compléter | US-27: validation of successful CRITICAL ticket creation, InvalidSeverity fault and TicketNotFound fault |
| 21 Nov | À compléter | Correction of ticketing-service/.dockerignore; verification that data/ and target/ are ignored by Git |
| 21 Nov | À compléter | Rebuild of ticketing-service Docker image and container health check; service successfully reached healthy status |
