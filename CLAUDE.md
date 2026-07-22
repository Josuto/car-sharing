# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

We're building the app described in @SPEC.MD. Read that file for general architectural tasks or to double-check the exact database structure, tech stack or application architecture.

Whenever working with any third-party library or something similar, you MUST look up the official documentation to ensure that you're working with up-to-date information. Use the DocsExplorer subagent for efficient documentation lookup.

Keep your replies extremely concise and focus on conveying the key information. No unnecessary fluff, no long code snippets.

Do not fabricate information, jump to rush conclusions or make wild assumptions. If you are unsure about something, ask me for clarification.

## Skills

Use the skills specified at the .claude/skills folder located at the root of the project as appropriate. Do not load extra skill resources (e.g., references, rules, examples, etc.) unless required.

| Skill name | When to use it |
|---|---|
| tdd | During solution implementation or refactoring |
| clean-code-principles | During solution planification or refactoring, when discussing design decisions |
| clean-ddd-hexagonal | During solution planification or refactoring, when discussing design decisions |
| effective-java | During solution implementation or refactoring |

## Build & Test Commands

```bash
# Build
./gradlew build

# Run
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.example.cs.SomeTest"

# Run a single test method
./gradlew test --tests "com.example.cs.SomeTest.methodName"

# Clean build
./gradlew clean build
```

## Architecture

This is a **multi-service car sharing platform**. The codebase is currently a skeleton being built out into the following services:

| Service | Responsibility |
|---|---|
| **Gateway** | Spring Cloud Gateway — routes all inbound HTTP requests |
| **User Management** | Admin CRUD for users; tracks debtor status |
| **Car Registry** | Car registration; publishes car events to RabbitMQ |
| **Car Booking** | Booking lifecycle; owns the Saga orchestrator |
| **Payments** | Account balances; integrates with mocked external banking service |

## Key Architectural Decisions

- **Hexagonal architecture** per service — domain logic isolated from infrastructure via ports/adapters.
- **READ = synchronous REST**, **WRITE = async via RabbitMQ**. The Booking service maintains a local copy of cars (synced from Registry via RabbitMQ) to avoid cross-service joins.
- **Saga pattern** lives in the Booking service: `PENDING → payment request → ACTIVE or CANCELLED`.
- **Strict DB isolation**: each service has its own SQLite schema; no cross-schema joins.
- **Pessimistic locking** on car rows to prevent double-booking.
- **CQRS read model** in Booking for the `GET /cars` (available cars) query.

## Base Package

`com.example.cs` — each service will live under its own sub-package (e.g., `com.example.cs.booking`, `com.example.cs.payments`).

## Tech Stack

- Java 21, Spring Boot 4.0.5, Gradle 9.4.1
- SQLite (one schema per service), RabbitMQ, Spring Cloud Gateway
- Docker, Kubernetes
- GitHub Actions (CI)
- JUnit
- Lombok
