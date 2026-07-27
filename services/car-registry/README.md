# Car Registry

## Goal

Manages the registration of cars into the platform's pool. Owners submit a car via REST; the service persists it and publishes a `CarRegistered` event so that the Car Booking service can maintain its local read model of available cars. No deletion is supported by design — once a car is registered, it stays in the pool.

## Input ports

### REST endpoints

Base path: `/cars` — exposed externally through the Gateway on port 8080.

#### POST /cars

Registers a new car.

**Request body:**
```json
{ "ownerId": "uuid", "type": "string", "registrationNumber": "string" }
```

`registrationNumber` must match the pattern `^\d{4}[A-Z]{3}$` (e.g. `1234ABC`).

**Response body:**
```json
{ "id": "uuid", "ownerId": "uuid", "type": "string", "registrationNumber": "string" }
```

| Status | Condition |
|---|---|
| `201 Created` | Car registered successfully |
| `400 Bad Request` | Validation failure or duplicate registration number |

### AMQP consumers

None.

## Output / side effects

### AMQP events published

Exchange: `car-events` (Direct). Routing key = event class simple name.

| Event | Routing key | Fields | Trigger |
|---|---|---|---|
| `CarRegistered` | `CarRegistered` | `id`, `ownerId`, `type`, `registrationNumber` | After a new car is successfully saved |

### Database writes

Database: `car-registry.db`. Table: `cars`.

| Column | Type | Notes |
|---|---|---|
| `id` | `String` (UUID) | PK |
| `ownerId` | `String` (UUID) | not null |
| `type` | `String` | not null |
| `registrationNumber` | `String` | unique, not null |

One INSERT per `POST /cars` call.

## Build / run / test

```bash
./gradlew :car-registry:build
./gradlew :car-registry:bootRun
./gradlew :car-registry:test
```

## Observability

No custom Micrometer metrics.
