# Car Booking

## Contents

1. [Goal](#goal)
2. [Input ports](#input-ports)
3. [Output / side effects](#output--side-effects)
4. [Build / run / test](#build--run--test)
5. [Infrastructure](#infrastructure)
6. [Observability](#observability)

## Goal

Owns the full booking lifecycle and is the home of the Saga orchestrator. When a borrower requests a booking, the service saves it as `PENDING` and kicks off an asynchronous payment flow; once the payment result arrives, the booking is confirmed (`ACTIVE`) or cancelled. The service also maintains a CQRS read model for available cars — a local projection synced from the Car Registry via RabbitMQ — so that `GET /cars` never requires a cross-service call. A scheduler detects overdue active bookings every 15 minutes and flags the corresponding borrowers as debtors. In a production-ready version this would run far less frequently (e.g. once a day).

## Input ports

### REST endpoints

Exposed externally through the Gateway on port 8080.

#### GET /cars

Returns the list of currently available cars (those with no active or pending booking).

**Response body:** JSON array
```json
[{ "id": "uuid", "type": "string" }]
```

| Status | Condition |
|---|---|
| `200 OK` | Always |

#### POST /bookings

Initiates the booking Saga for a car.

**Request body:**
```json
{ "carId": "uuid", "borrowerId": "uuid", "startDate": "yyyy-MM-dd", "endDate": "yyyy-MM-dd" }
```

Maximum booking duration: 15 days.

**Response body:**
```json
{ "id": "uuid", "carId": "uuid", "borrowerId": "uuid", "startDate": "yyyy-MM-dd", "endDate": "yyyy-MM-dd", "status": "PENDING" }
```

| Status | Condition |
|---|---|
| `201 Created` | Booking created with status `PENDING`; Saga initiated |
| `400 Bad Request` | `{ "error": "..." }` — borrower is a debtor, borrower has an ongoing booking, car not found, car not available, or invalid date range |

#### PUT /bookings/{id}

Returns a booked car, finalising the booking.

**Response body:** same `BookingResponse` shape with `status: "RETURNED"`.

| Status | Condition |
|---|---|
| `200 OK` | Car returned; booking status set to `RETURNED` |
| `400 Bad Request` | `{ "error": "..." }` — booking is not in `ACTIVE` status |
| `404 Not Found` | Booking not found |

### AMQP consumers

#### CarRegisteredConsumer

| Property | Value |
|---|---|
| Queue | `booking.car-registered` |
| Exchange | `car-events` (Direct) |
| Routing key | `CarRegistered` |
| Event fields | `id`, `ownerId`, `type`, `registrationNumber` |

Saves a local car projection (`id`, `type`) to the `cars` table.

#### UserCreatedConsumer

| Property | Value |
|---|---|
| Queue | `booking.user-created` |
| Exchange | `user-events` (Direct) |
| Routing key | `UserCreated` |
| Event fields | `id`, `username`, `name`, `surname`, `bankAccount` |

Saves a local user projection (`id`, `isDebtor = false`) to the `users` table.

#### UserDebtorConsumer

| Property | Value |
|---|---|
| Queue | `booking.borrower-flagged-as-debtor` |
| Exchange | `user-events` (Direct) |
| Routing key | `BorrowerFlaggedAsDebtor` |
| Event fields | `userId` |

Updates the local user projection to `isDebtor = true`.

#### PaymentResultConsumer

| Property | Value |
|---|---|
| Queue | `booking.payment-processed` |
| Exchange | `payment-events` (Direct) |
| Routing key | `PaymentProcessed` |
| Event fields | `bookingId`, `success` |

Completes the Saga: sets booking status to `ACTIVE` (success) or `CANCELLED` (failure).

## Output / side effects

### AMQP events published

Exchange: `booking-events` (Direct). Routing key = event class simple name.

| Event | Routing key | Fields | Trigger |
|---|---|---|---|
| `BookingPaymentRequested` | `BookingPaymentRequested` | `bookingId`, `borrowerId`, `carId`, `startDate`, `endDate` | After booking saved as `PENDING` (Saga step 1) |
| `BorrowerFlaggedAsDebtor` | `BorrowerFlaggedAsDebtor` | `userId` | Every 15 min — for each borrower with an overdue `ACTIVE` booking who is not yet a debtor |

### Database writes

Database: `car-booking.db`.

**Table: `bookings`**

| Column | Type |
|---|---|
| `id` | `String` (UUID, PK) |
| `carId` | `String` (UUID) |
| `borrowerId` | `String` (UUID) |
| `startDate` | `LocalDate` |
| `endDate` | `LocalDate` |
| `status` | `String` (`PENDING` / `ACTIVE` / `RETURNED` / `CANCELLED`) |
| `createdAt` | `long` (epoch ms) |

| Handler | Status written |
|---|---|
| `CreateBookingHandler` | `PENDING` |
| `PaymentResultHandler` (success) | `ACTIVE` |
| `PaymentResultHandler` (failure) | `CANCELLED` |
| `ReturnCarHandler` | `RETURNED` |

**Table: `cars`** — local read-model projection

| Column | Type |
|---|---|
| `id` | `String` (UUID, PK) |
| `type` | `String` |

Written by `CarRegisteredConsumer` on each `CarRegistered` event.

**Table: `users`** — local read-model projection

| Column | Type |
|---|---|
| `id` | `String` (UUID, PK) |
| `isDebtor` | `boolean` |

Written by `UserCreatedConsumer` (insert, `isDebtor = false`), `UserDebtorConsumer` and `FlagLateReturnDebtorsHandler` (update, `isDebtor = true`).

### Scheduled tasks

`LateReturnDebtorScheduler` runs **every 15 minutes** (`fixedRate = 15 min`). It delegates to `FlagLateReturnDebtorsHandler`, which finds all borrowers with overdue `ACTIVE` bookings, flags them locally, and publishes a `BorrowerFlaggedAsDebtor` event per borrower. In a production-ready version this would run far less frequently (e.g. once a day at midnight).

## Build / run / test

```bash
./gradlew :car-booking:build
./gradlew :car-booking:bootRun
./gradlew :car-booking:test
```

## Infrastructure

Manifest: `infra/car-booking.yaml`

| Resource | Kind | Details |
|---|---|---|
| `car-booking-pvc` | PersistentVolumeClaim | 256 Mi; `ReadWriteOnce`; mounted at `/app/data` |
| `car-booking` | Deployment | 1 replica; image `car-sharing/car-booking:latest`; port 8083; env from `car-sharing-config` ConfigMap + `OTEL_SERVICE_NAME=car-booking` |
| `car-booking` | Service | `ClusterIP`; port 8083 — internal only, accessed via Gateway |

## Observability

| Metric | Type | Description |
|---|---|---|
| `bookings.active.current` | Gauge | Live count of bookings in `ACTIVE` status |
| `bookings.pending.current` | Gauge | Live count of bookings in `PENDING` status |
| `bookings.created.total` | Counter | Incremented once per successful booking creation |
| `cars.available` | Gauge | Count of cars currently available for booking |
| `bookings.saga.duration` | Timer | Duration from booking creation to `PaymentProcessed` receipt |
