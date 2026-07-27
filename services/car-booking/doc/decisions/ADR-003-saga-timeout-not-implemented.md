# ADR-003: No Saga timeout for stuck PENDING bookings

## Status
Accepted

## Date
2026-06-02

## Context
The booking Saga works as follows: `CreateBookingHandler` persists the booking in `PENDING` state
and emits `BookingPaymentRequested`. `PaymentResultConsumer` then receives `PaymentProcessed` and
transitions the booking to `ACTIVE` or `CANCELLED`.

If `PaymentProcessed` never arrives (e.g., the Payments service is down or the message is lost),
the booking remains `PENDING` indefinitely. While in `PENDING`, the car is treated as unavailable
by the `GET /cars` query, so a stuck booking silently removes a car from the available pool.

## Decision
Accept the stuck-PENDING scenario for MVP. No timeout, scheduled cleanup, or compensating
transaction is implemented. The Saga has no self-healing mechanism.

## Alternatives Considered

### Scheduled cleanup job
A periodic task (e.g., Spring `@Scheduled`) cancels any booking that has been in `PENDING` for
longer than a configurable threshold (e.g., 5 minutes).

- **Pros:** Simple to implement; restores car availability automatically.
- **Cons:** Introduces a time-based heuristic that may cancel legitimate bookings under transient
  load. Requires careful threshold tuning.
- **Deferred:** Valid improvement for a post-MVP iteration.

### Dead-letter queue (DLQ) + redelivery
Configure RabbitMQ with a DLQ and redelivery policy so that unprocessed `BookingPaymentRequested`
events are retried or surfaced for manual inspection.

- **Pros:** Increases reliability of message delivery; reduces the window in which a booking can
  get stuck.
- **Cons:** Adds RabbitMQ topology complexity. Already noted as a potential improvement in
  SPEC.md §7.
- **Deferred:** Out of scope for MVP; referenced in SPEC.md improvements section.

### Key-value store with TTL (e.g., AWS DynamoDB)
Store PENDING bookings in a KV store with a native TTL field. Once the TTL expires, the record
is automatically removed, making the car available again without any polling logic.

- **Pros:** Zero-code expiry; no scheduled job or heuristic threshold needed. A well-established
  pattern for ephemeral state in distributed systems.
- **Cons:**
  - **Query model breaks:** The `GET /cars` availability query performs a single SQL join over
    `cars` and `bookings` to exclude PENDING/ACTIVE cars. Splitting PENDING bookings into a KV
    store and terminal-state bookings into SQLite would require a cross-store merge, adding
    significant complexity.
  - **Late-arrival race:** If the TTL fires and removes the PENDING booking before
    `PaymentProcessed` arrives, the Saga has no booking to transition. Handling orphaned events
    requires tombstoning or idempotency logic, which offsets the simplicity gain.
  - **Infrastructure mismatch:** The project runs locally on Docker/K8s with SQLite. Adding
    DynamoDB introduces an AWS dependency (or LocalStack), which is disproportionate overhead
    for a pet project.
- **Rejected:** The unified SQL availability query is the primary constraint; splitting the
  booking store across two backends is not justified here.

## Consequences
- A booking can remain `PENDING` indefinitely if `PaymentProcessed` is never received.
- The affected car will appear unavailable until the booking is manually cancelled or the database
  is corrected.
- This is acceptable for a pet project that prioritises simplicity over operational resilience.
- If this service were to move toward production use, ADR-003 should be revisited in favour of
  the scheduled cleanup or DLQ alternative.
