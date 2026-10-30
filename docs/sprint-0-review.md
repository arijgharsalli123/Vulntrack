# Sprint 0 – Review and retrospective

**Dates:** 28–30 October 2026
**Sprint goal:** frame the project (subject sheet, repository, contracts v1, backlog) and prove the Docker chain.

## Review (what was delivered)

| Story | Result | Evidence |
|---|---|---|
| US-01 Repository initialised | Done | Commit "chore: init repository structure" |
| US-02 Contracts v1 | Done | `ticket.xsd` (PR #1), `scan.proto` (PR #2), `docs/rest-endpoints.md` (PR #3) |
| US-03 Compose skeleton | Done (planned for Sprint 1, finished early) | 3 PostgreSQL databases + asset-service, all healthy under Docker Compose |
| Subject sheet | Sent to the teacher | 30 October 2026 |
| Board and Sprint 1 backlog | Ready | GitHub Project, issues US-03 to US-09 |

**Points done:** 10 (US-01: 2, US-02: 5, US-03: 3)
**Real time spent:** 4 hours

## Decisions taken

- **D1:** vulnerability-service pulls the gRPC stream from scan-service, triggered by asset-service (REST call `POST /api/v1/scan-imports`).
- **Ports:** asset-service on 8083 everywhere; report-service moves to 8081.
- Work is done on one `feature/US-xx` branch per story, merged by pull request.

## Retrospective

**What went well**
- Contract-first: the three contracts were written before any service code.
- A clean Git history with conventional commit messages and merged pull requests.
- The Docker chain (multi-stage build, healthchecks, `depends_on`) was proven on day 3.

**What slowed me down**

une version de Spring Boot introuvable, une erreur Docker

**One improvement for Sprint 1**
- Log the time spent after each work session in `docs/time-log.md`, so the November 6 checkpoint is based on real numbers.

## Sprint 1 plan (31 Oct – 6 Nov, about 5 hours)

**Goal:** ticketing-service (SOAP, contract-first) works on its own inside Docker.
**Order:** US-04, US-05, US-06 (ticketing-service), then US-07 to US-09 (scan-service) if time allows.
**Checkpoint:** 6 November, compare planned versus real velocity and decide what to cut.