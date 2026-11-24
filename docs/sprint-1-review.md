# Sprint 1 – Review and retrospective

**Dates:** planned 31 October – 6 November 2026, actually completed on 23 November 2026 
**Sprint goal:** the two risky services (SOAP and gRPC) work on their own inside Docker.
**Result:** goal reached. ticketing-service (SOAP) and scan-service (gRPC) start under Docker Compose, report `healthy`, and scan-service keeps its data after a restart.

## Review (what was delivered)

| Story | Result | Evidence |
|---|---|---|
| US-04 Ticket XSD and generated WSDL | Done | `ticket.xsd` written first (contract-first); WSDL served at `/ws/tickets.wsdl` |
| US-05 `createTicket` and `getTicketStatus` | Done | SOAP samples in `postman/SOAP/`; SOAP integration tests; ticketing-service `healthy` under Compose |
| US-06 SOAP Faults | Done | `TicketNotFound` and `InvalidSeverity` faults covered by integration tests |
| US-07 Versioned `.proto` and gRPC service (unary + streaming) | Done | `StartScan`, `GetScanStatus`, `StreamFindings` checked with grpcurl and a gRPC integration test |
| US-08 Scanner simulation | Done | 3 to 10 fake CVE (`CVE-2099-xxxx`) per scan, at least one CRITICAL, pause between findings |
| US-09 gRPC statuses | Done (server side) | `INVALID_ARGUMENT` and `NOT_FOUND` tested; `DEADLINE_EXCEEDED` and `UNAVAILABLE` are produced on the client side (Sprint 2) |

**Stories that run across several sprints (counted when finished):**

| Story | State |
|---|---|
| US-25 Multi-stage Dockerfile per service | 3 of 6 done (asset, ticketing, scan) |
| US-26 Complete Compose | Partial: 3 PostgreSQL, asset, ticketing, scan |
| US-27 Unit tests | Ticketing and scan done |
| US-28 One integration test per style | SOAP and gRPC done; REST missing |
| US-22 Actuator and stdout logs | asset, ticketing, scan |

**Points done in this sprint:** 16 (US-04: 3, US-05: 3, US-06: 2, US-07: 3, US-08: 3, US-09: 2)
**Cumulative:** 26 of 108 mandatory points
**Real time spent:** about 11.5 h (ticketing U1–U9 about 4.5 h, scan-service logic 3.5 h, scan-service tests and Docker about 3.5 h including the grpcurl installation). The time log is being reconstructed from the Git history.

**Velocity:** about 1.4 points/hour counting only finished stories, about 2.3 counting the parts already delivered of US-22, US-25, US-26, US-27 and US-28.
**Needed for the rest:** about 70 effective points in 15 working days of 3.5 h, so about 1.3 points/hour. The margin is thin on the cautious reading and comfortable on the other.

## Decisions taken

- **D2:** Spring Boot 4.1.2 (from the `pom.xml`) is kept instead of 3.3.x written in the specification. The specification is aligned.
- **D3:** `contracts/` is the source of truth. `ticket.xsd` and `scan.proto` are copied into their service because the Docker build context cannot read `../contracts`. Check with `diff` before each pull request.
- **D4:** the scan simulator is deterministic per `assetId` (same asset, same CVE) and every scan contains a CRITICAL finding. This makes the duplicate check and the ticket demo reproducible.
- **D5:** the gRPC server produces `INVALID_ARGUMENT` and `NOT_FOUND`. `DEADLINE_EXCEEDED` and `UNAVAILABLE` come from the clients (asset-service, vulnerability-service).
- **D6:** each service is layered: entry point (SOAP endpoint or gRPC service), business service, repository. Business exceptions are translated into protocol errors only at the entry point.
- **Ports:** scan-service gRPC on 9090, Actuator on 8084; ticketing-service on 8085.

## Known limits (for the report)

- Spring-WS does not validate payloads against the XSD by default: an invalid severity is caught by a business rule only. `TicketFaultDetail` from the XSD is not used in the Faults yet.
- A scan stays `RUNNING` until a client consumes `StreamFindings`; an abandoned scan is never closed.
- gRPC runs without TLS and the service ports are published on the host for development. Only the gateway should stay exposed in the final Compose.
- The ticketing `contextLoads` test still uses the file H2 database; it should move to an in-memory database.

## Retrospective

**What went well**
- Contract-first again: the `.proto` was written before the code, and the SOAP and gRPC contracts each have an integration test.
- The layered structure made the business logic testable without any network (unit tests with no gRPC server).
- Persistence was checked after a container restart before committing.
- Time estimates per 30-minute unit were reliable for the scan-service work.

**What slowed me down**
- Installing grpcurl on Windows and getting the quoting of the JSON argument right in PowerShell and cmd.
- A pause on day 1 to understand the role of each class before going on.
- The Sprint 1 checkpoint planned for 6 November was not held, and the time log stopped on 30 October, so the Sprint 0 improvement (log time after each session) was only partly applied.

**One improvement for Sprint 2**
- Keep the working grpcurl commands as ready-to-run scripts in `postman/` (they are needed for US-29 anyway), and fill `docs/time-log.md` at the end of every working day.

## Sprint 2 plan (25 November – 1 December, 6 working days)

**Goal:** first end-to-end flow through the gateway: asset, scan, vulnerabilities stored.
**Order:** vulnerability-service skeleton, gRPC import with duplicate check, REST consultation and filters, asset-service CRUD, scan launch (REST to gRPC with deadline), gateway routes.
**Stories:** US-10, US-11, US-12, US-13, US-14, US-15, US-19 (plus the shared parts of US-22, US-23, US-25).
**Checkpoint:** 1 December review. If a day slips, cut in this order: bonus, then the report-service details, then US-24.