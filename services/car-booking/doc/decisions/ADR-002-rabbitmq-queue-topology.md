# ADR-002: One queue per event for Booking service RabbitMQ consumers

## Status
Accepted

## Date
2026-05-25

## Context
The Booking service consumes three events from upstream services, routed via direct exchanges
using the event class simple name as the routing key:

| Event | Exchange | Routing key |
|---|---|---|
| `CarRegistered` | `car-events` | `CarRegistered` |
| `UserCreated` | `user-events` | `UserCreated` |
| `BorrowerFlaggedAsDebtor` | `user-events` | `BorrowerFlaggedAsDebtor` |

The Booking service owns its queue and binding declarations (and re-declares the upstream
exchanges idempotently) so that it is self-sufficient.

## Decision
Declare one durable queue per event, named `booking.<event-in-kebab-case>`:
`booking.car-registered`, `booking.user-created`, `booking.borrower-flagged-as-debtor`.

## Alternatives Considered

### Single shared queue bound to all routing keys
One `booking` queue bound to all three routing keys across both exchanges.

- **Pros:** Fewer resources; simpler infrastructure setup.
- **Cons:** A single listener receives mixed event types and needs runtime type dispatch.
  Per-event throughput and dead-lettering are harder to monitor and configure independently.
- **Rejected:** The per-event approach is cleaner and costs nothing at this scale.

## Consequences
- Each `@RabbitListener` is bound to exactly one queue, handling one event type with no
  type-dispatch logic.
- Adding a new consumed event requires a new queue declaration and a new listener — changes
  stay local and explicit.
