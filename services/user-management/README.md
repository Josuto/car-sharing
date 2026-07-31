# User Management

## Contents

1. [Goal](#goal)
2. [Input ports](#input-ports)
3. [Output / side effects](#output--side-effects)
4. [Build / run / test](#build--run--test)
5. [Infrastructure](#infrastructure)
6. [Observability](#observability)

## Goal

Manages the user lifecycle for the car-sharing platform. Admins can create, update, and soft-delete users via unauthenticated REST endpoints. The service also tracks debtor status: when the Car Booking service flags a borrower for a late return, this service marks the corresponding user record as a debtor.

## Input ports

### REST endpoints

Base path: `/users` — exposed externally through the Gateway on port 8080.

#### POST /users

Creates a new user.

**Request body:**
```json
{ "username": "string", "name": "string", "surname": "string", "bankAccount": "string" }
```

**Response body:**
```json
{ "id": "uuid", "username": "string", "name": "string", "surname": "string", "bankAccount": "string" }
```

| Status | Condition |
|---|---|
| `201 Created` | User created successfully |
| `400 Bad Request` | Any field is null or blank |

#### PUT /users/{id}

Updates an existing user's name, surname, and/or bank account.

**Request body:**
```json
{ "name": "string", "surname": "string", "bankAccount": "string" }
```

**Response body:** same shape as `POST /users`.

| Status | Condition |
|---|---|
| `200 OK` | User updated |
| `400 Bad Request` | Any field is null or blank |
| `404 Not Found` | User not found |

#### DELETE /users/{id}

Soft-deletes a user (sets `isDeleted = true`; the row is retained).

| Status | Condition |
|---|---|
| `204 No Content` | User soft-deleted |
| `404 Not Found` | User not found |

### AMQP consumers

#### BorrowerFlaggedConsumer

| Property | Value |
|---|---|
| Queue | `user-management.borrower-flagged-as-debtor` |
| Exchange | `booking-events` (Direct) |
| Routing key | `BorrowerFlaggedAsDebtor` |
| Event fields | `userId: String` |

Sets `isDebtor = true` for the referenced user. Idempotent: no-op if the user is already a debtor or does not exist locally.

## Output / side effects

### AMQP events published

Exchange: `user-events` (Direct). Routing key = event class simple name.

| Event | Routing key | Fields | Trigger |
|---|---|---|---|
| `UserCreated` | `UserCreated` | `id`, `username`, `name`, `surname`, `bankAccount` | After a new user is successfully saved |
| `UserBankAccountChanged` | `UserBankAccountChanged` | `userId`, `bankAccount` | After `PUT /users/{id}` when `bankAccount` differs from the stored value |

### Database writes

Database: `user-management.db`. Table: `users`.

| Column | Type | Notes |
|---|---|---|
| `id` | `String` (UUID) | PK |
| `username` | `String` | unique, not null |
| `name` | `String` | not null |
| `surname` | `String` | not null |
| `bankAccount` | `String` | not null |
| `isDebtor` | `boolean` | default `false` |
| `isDeleted` | `boolean` | default `false` |

| Handler | Operation |
|---|---|
| `CreateUserHandler` | INSERT new user row |
| `UpdateUserHandler` | UPDATE `name`, `surname`, `bankAccount` |
| `DeleteUserHandler` | UPDATE `isDeleted = true` |
| `UpdateDebtorStatusHandler` | UPDATE `isDebtor = true` |

## Build / run / test

```bash
./gradlew :user-management:build
./gradlew :user-management:bootRun
./gradlew :user-management:test
```

## Infrastructure

Manifest: `infra/user-management.yaml`

| Resource | Kind | Details |
|---|---|---|
| `user-management-pvc` | PersistentVolumeClaim | 256 Mi; `ReadWriteOnce`; mounted at `/app/data` |
| `user-management` | Deployment | 1 replica; image `car-sharing/user-management:latest`; port 8081; env from `car-sharing-config` ConfigMap + `OTEL_SERVICE_NAME=user-management` |
| `user-management` | Service | `ClusterIP`; port 8081 — internal only, accessed via Gateway |

## Observability

| Metric | Type | Description |
|---|---|---|
| `users.debtors.current` | Gauge | Live count of non-deleted users with `isDebtor = true` |
