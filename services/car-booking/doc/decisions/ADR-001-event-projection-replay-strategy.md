# ADR-001: No event replay for local car and user projections

## Status
Accepted

## Date
2026-05-25

## Context
The Booking service maintains local SQLite projections of cars (`LocalCar`) and users (`LocalUser`),
populated by consuming RabbitMQ events (`CarRegistered`, `UserCreated`, `BorrowerFlaggedAsDebtor`).
SQLite is file-based, so these projections survive normal service restarts.

However, RabbitMQ is a message broker, not an event log: once a message is consumed it is gone,
and if no queue existed at publish time the event is lost entirely. This means that on first
deployment (or after a database wipe), the local projections start empty and will only be
populated by events that arrive *after* the service is running.

## Decision
Accept the data gap. Do not implement any mechanism to seed or replay projections on startup.
Cars and users registered before the Booking service was deployed will not appear in `GET /cars`
until new events flow in or the data is manually seeded. SQLite persistence is sufficient to
handle normal restarts without any replay logic.

## Alternatives Considered

### Sync-on-startup via REST
On startup, call Car Registry (`GET /cars`) and User Management (`GET /users`) to fetch all
existing data and populate the local projections before accepting traffic.

- **Pros:** Projections are complete immediately after startup; no data gap in practice.
- **Cons:** Introduces synchronous inter-service coupling at boot time, which violates the
  async-first architectural principle of the platform. It also requires the upstream services
  to expose list endpoints, and adds retry/failure-handling logic to the startup path.
- **Rejected:** The coupling cost outweighs the benefit for a project where projection gaps
  are an acceptable edge case.

### Persistent event log / event sourcing
Store every domain event in a durable append-only log (e.g., Kafka, EventStoreDB, or a custom
outbox table). Consumers can replay the full log from the beginning on startup.

- **Pros:** Full auditability and replay capability; projections can always be rebuilt from
  scratch.
- **Cons:** Significant infrastructure overhead (new broker or schema, retention policies,
  consumer offset management). Overkill for the current scale and complexity.
- **Rejected:** The operational complexity is not justified for a pet project.

## Consequences
- `GET /cars` may return an incomplete list on first deployment until events flow in naturally.
- The limitation is acceptable for a pet project that prioritises solution simplicity over
  operational completeness.
- If this service were to move toward production use, ADR-001 should be revisited in favour
  of the sync-on-startup or event-sourcing alternative.
