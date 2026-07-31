# PSP Stub

## Contents

1. [Goal](#goal)
2. [Input ports](#input-ports)
3. [Output / side effects](#output--side-effects)
4. [Build / run / test](#build--run--test)
5. [Infrastructure](#infrastructure)
6. [Observability](#observability)

## Goal

A lightweight Spring Boot service that simulates the external Payment Service Provider (PSP) used by the Payments service. It holds in-memory account balances seeded at startup and applies the same HTTP contract as a real PSP — `POST /process` decrements balances and returns `409` when funds are insufficient. No code inside the Payments service distinguishes between the stub and a real PSP. See [ADR-002](../../doc/decisions/ADR-002-psp-stub-as-external-service-simulator.md) for the full rationale.

## Input ports

### REST endpoints

Port: 8085.

#### POST /process

Attempts a payment charge against the given bank account.

**Request body:**
```json
{ "bankAccount": "string", "amount": "decimal", "currency": "string" }
```

**Response body:** none (body-less response).

| Status | Condition |
|---|---|
| `200 OK` | Account exists and has sufficient balance; balance is decremented in memory |
| `404 Not Found` | `bankAccount` not found in the stub store |
| `409 Conflict` | Account exists but balance is insufficient |

#### GET /balances

Returns the current in-memory balance for all seeded accounts. Useful for manual inspection during development.

**Response body:**
```json
{ "ES341234567890": 30.00, "ES341234567891": 0.00, "ES341234567892": 200.00 }
```

| Status | Condition |
|---|---|
| `200 OK` | Always |

### AMQP consumers

None.

## Output / side effects

### AMQP events published

None.

### Database writes

None. All state is held in a `BalanceStore` backed by a `HashMap<String, BigDecimal>` seeded at startup with:

| Bank account | Initial balance |
|---|---|
| `ES341234567890` | €30.00 |
| `ES341234567891` | €0.00 |
| `ES341234567892` | €200.00 |

State is lost on pod restart, which is acceptable for a simulator.

## Build / run / test

```bash
./gradlew :psp-stub:build
./gradlew :psp-stub:bootRun
./gradlew :psp-stub:test
```

## Infrastructure

Manifest: `infra/psp-stub.yaml`

| Resource | Kind | Details |
|---|---|---|
| `psp-stub` | Deployment | 1 replica; image `car-sharing/psp-stub:latest`; port 8085; no ConfigMap (no AMQP, no OTel) |
| `psp-stub` | Service | `ClusterIP`; port 8085 — internal only, accessed by Payments at `http://psp-stub:8085` |

## Observability

No custom Micrometer metrics.
