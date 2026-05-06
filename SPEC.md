# Technical Specification Document (TSD)

## 1. Overview

App enables owners to lend cars to borrowers. MVP focuses on core backend loops without frontend. Includes mocked external banking service.

**Core Features:** User management, car registration, car booking, payments.

**Tech Stack & Infrastructure:** Java Spring Boot based multi-module application with Gradle as package manager, SQLite, Kafka, Spring Cloud Gateway, Docker, Kubernetes (local execution), GitHub Actions (CI).

---

## 2. Architecture

**High-Level Overview:** Gateway routes REST (HTTP) requests. READs are synchronous REST. WRITEs propagate asynchronously via Kafka. Payments service integrates with a mocked external banking service for balance processing.

**Hexagonal Architecture:** Ports/adapters isolate domain logic from infrastructure.

**Database Boundaries:** Strict isolation per service (separate DB schemas). Booking implements a CQRS read-model to optimize queries.

---

## 3. Functional Requirements

### 3.1 User Flows & Business Features

#### User Management Service
- Admins create, update, delete users via unauthenticated endpoints.
- Manages user identity (username, name, surname, debtor status).

#### Car Registry Service
- Owners register cars (type, registration number).
- No deletion allowed.

#### Car Booking Service
- Provides list of available cars.
- Processes bookings (1–15 days max). Enforces 1 active booking per borrower.
- **Handle returns & availability:** Every time a borrower books a car, a new booking instance is created. This instance is updated with any booking-related event (e.g., returned, cancelled due to insufficient funds). 
- The logic to obtain available cars searches over all bookings to determine which cars display availability in their status.
- Unused days not refunded. Flags borrower as debtor via User Management if returned late.

#### Payments Service
- Manages user account balances.
- Calculates booking fee (10 EUR × days).
- Deducts fee from borrower account balance. Integrates with mocked external banking transfer processing service.

### 3.2 Implementation Details

- **Data Synchronization:** Booking service keeps synchronized local DB copy of cars from Registry via Kafka.
- **Concurrency:** Pessimistic DB locking prevents double-booking identical cars.
- **Saga Orchestrator:** Located in Booking Service. Flow: Receives booking → Sets Pending → Emits async payment request → Listens for payment result → Confirms or Cancels booking.
- **Security:** Generates UUIDs for entities. Returns generic 400 errors for invalid bookings.

---

## 4. Non-Functional Requirements

| Concern | Approach |
|---|---|
| Performance | Local car copy in Booking prevents synchronous cross-service joins. |
| Security | UUID primary keys prevent enumeration attacks. |
| Reliability | Saga pattern ensures distributed transaction integrity. Kafka handles async delivery. |
| Maintainability | Single Responsibility Principle. Independent schemas. Hexagonal architecture. |

---

## 5. Data Model and Database Schema

### User Management DB

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR UNIQUE,
    name VARCHAR,
    surname VARCHAR,
    is_debtor BOOLEAN DEFAULT FALSE
);
```

### Car Registry DB

```sql
CREATE TABLE cars (
    id UUID PRIMARY KEY,
    owner_id UUID,
    type VARCHAR,
    registration_number VARCHAR UNIQUE
);
```

### Car Booking DB

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    is_debtor BOOLEAN DEFAULT FALSE
);

CREATE TABLE cars (
    id UUID PRIMARY KEY,
    type VARCHAR
);

CREATE TABLE bookings (
    id UUID PRIMARY KEY,
    car_id UUID,
    borrower_id UUID,
    start_date DATE,
    end_date DATE,
    status VARCHAR -- PENDING, ACTIVE, RETURNED, CANCELLED
    FOREIGN KEY (car_id) REFERENCES cars(id)
    FOREIGN KEY (borrower_id) REFERENCES users(id)
);
```

### Payments DB

```sql
CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    user_id UUID,
    balance DECIMAL(10,2)
);

CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    booking_id UUID,
    borrower_id UUID,
    amount DECIMAL(10,2),
    status VARCHAR -- SUCCESS, FAILED
);
```

---

## 6. Gateway API

### User Management

| Method | Path | Description | Responses |
|---|---|---|---|
| `POST` | `/users` | Create new user | `201 Created`, `400 Bad Request` |
| `PUT` | `/users/{id}` | Update user details | `200 OK`, `400 Bad Request`, `404 Not Found` |
| `DELETE` | `/users/{id}` | Delete user | `204 No Content`, `404 Not Found` |

### Car Registry

| Method | Path | Description | Responses |
|---|---|---|---|
| `POST` | `/cars` | Register new car into the pool | `201 Created`, `400 Bad Request` |

### Car Booking

| Method | Path | Description | Responses |
|---|---|---|---|
| `GET` | `/cars` | List all currently available cars | `200 OK` |
| `POST` | `/bookings` | Submit booking request | `201 Created` (Saga initiated), `400 Bad Request` |
| `PUT` | `/bookings/{id}` | Return a booked car | `200 OK`, `400 Bad Request`, `404 Not Found` |

---

## 7. Potential Issues and Improvements

### Potential Issues

- Unauthenticated user management endpoints pose a security risk, even in MVP.
- Eventual consistency delay between a car being registered/returned and it appearing in the GET `/cars` response due to Kafka propagation. Possible complementary solution approaches include Optimistic UI (frontend), Double-Checks (backend integrity), and Versioning (Kafka ordering).

### Improvements

- Add basic API Key auth to Gateway for admin endpoints.
- Define a dead-letter queue (DLQ) strategy in Kafka or the Outbox pattern for failed sync events between Registry and Booking services.
- Define explicit mock responses and latency profiles for the external banking transfer processing service to simulate real-world timeouts.
