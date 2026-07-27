# Payments

## Goal

Manages borrower accounts and processes booking payments. The service is entirely event-driven — it has no REST endpoints. When a user is created, the service opens a local account record. When a booking payment is requested, it calculates the fee (€10 × days), calls the external PSP, records the transaction, and publishes the outcome back to the Car Booking service to complete the Saga.

## Input ports

### REST endpoints

None.

### AMQP consumers

#### UserCreatedConsumer

| Property | Value |
|---|---|
| Queue | `payments.user-created` |
| Exchange | `user-events` (Direct) |
| Routing key | `UserCreated` |
| Event fields | `id`, `username`, `name`, `surname`, `bankAccount` |

Creates a new `Account` for the user if one does not already exist.

#### UserBankAccountChangedConsumer

| Property | Value |
|---|---|
| Queue | `payments.user-bank-account-changed` |
| Exchange | `user-events` (Direct) |
| Routing key | `UserBankAccountChanged` |
| Event fields | `userId`, `bankAccount` |

Updates the stored `bankAccount` on the borrower's account record.

#### PaymentRequestedConsumer

| Property | Value |
|---|---|
| Queue | `payments.booking-payment-requested` |
| Exchange | `booking-events` (Direct) |
| Routing key | `BookingPaymentRequested` |
| Event fields | `bookingId`, `borrowerId`, `carId`, `startDate`, `endDate` |

Calculates the booking fee, calls the PSP, records the transaction, and publishes `PaymentProcessed`.

## Output / side effects

### AMQP events published

Exchange: `payment-events` (Direct). Routing key = event class simple name.

| Event | Routing key | Fields | Trigger |
|---|---|---|---|
| `PaymentProcessed` | `PaymentProcessed` | `bookingId`, `success` | After every PSP call, regardless of outcome |

### Database writes

Database: `payments.db`.

**Table: `accounts`**

| Column | Type | Notes |
|---|---|---|
| `id` | `String` (UUID, PK) | |
| `userId` | `String` (UUID) | not null |
| `bankAccount` | `String` | not null |

`CreateAccountHandler` inserts on `UserCreated`; `UpdateBankAccountHandler` upserts on `UserBankAccountChanged`.

**Table: `transactions`**

| Column | Type | Notes |
|---|---|---|
| `id` | `String` (UUID, PK) | |
| `bookingId` | `String` (UUID) | not null |
| `borrowerId` | `String` (UUID) | not null |
| `amount` | `BigDecimal` | not null |
| `currency` | `String` | not null |
| `status` | `String` | `SUCCESS`, `INSUFFICIENT_FUNDS`, or `PSP_ERROR` |

One INSERT per `BookingPaymentRequested` event.

### External HTTP calls

Calls the PSP on every `BookingPaymentRequested` event.

| Property | Value |
|---|---|
| Base URL | `${psp.base-url}` (k8s: `http://psp-stub:8085`) |
| Endpoint | `POST /process` |
| Request fields | `bankAccount: String`, `amount: BigDecimal`, `currency: String` |
| `200 OK` | → `TransactionStatus.SUCCESS` |
| `409 Conflict` | → `TransactionStatus.INSUFFICIENT_FUNDS` |
| Any other non-2xx | → `TransactionStatus.PSP_ERROR` |

Response body is discarded; the status code alone determines the outcome.

## Build / run / test

```bash
./gradlew :payments:build
./gradlew :payments:bootRun
./gradlew :payments:test
```

## Observability

| Metric | Type | Tag | Description |
|---|---|---|---|
| `bookings.outcome` | Counter | `result=success` | PSP returned `200 OK` |
| `bookings.outcome` | Counter | `result=insufficient_funds` | PSP returned `409 Conflict` |
| `bookings.outcome` | Counter | `result=psp_error` | PSP returned any other non-2xx |
