# ADR-002: PSP stub as a local simulator for the external Payment Service Provider

## Status
Accepted

## Date
2026-07-27

## Context
The Payments service integrates with an external Payment Service Provider (PSP) over HTTP (`POST /process`). In production this would be a third-party banking API. During local development and automated testing, calls to a real PSP are impractical: the endpoint is unavailable, responses are non-deterministic, and triggering real financial transactions is unsafe.

A substitute was needed that satisfies the Payments service's HTTP contract without introducing a test-only code path inside Payments itself.

## Decision
A dedicated `psp-stub` Spring Boot service is deployed alongside the real services in the local Kubernetes cluster. It exposes two endpoints:

- `POST /process` — simulates a payment attempt; returns `200 OK` on success or `409 Conflict` when the account has insufficient funds.
- `GET /balances` — returns current in-memory balances for all accounts, allowing manual inspection during development.

Account balances are held in memory (`BalanceStore`) and seeded at startup. The Payments service points to `psp-stub` via the `psp.base-url` configuration property; no code inside Payments distinguishes between the stub and a real PSP.

## Alternatives Considered

### WireMock or similar HTTP mock library
A WireMock instance could stub the PSP responses at the test level without deploying an extra service.

Rejected: WireMock is confined to the test classpath and cannot serve the running cluster. An always-on stub service better reflects the real deployment topology and lets the full system be exercised end-to-end without test harness involvement.

### Third-party API mock tools (e.g. Mockoon, Stoplight Prism)
Tools such as Mockoon CLI (deployable as a Docker container) can stub HTTP endpoints from a configuration file with no custom code.

Rejected: the PSP contract requires stateful behaviour — `POST /process` must check and decrement an in-memory account balance and return `409` when funds are insufficient. Generic mock tools return static or template-scripted responses; replicating mutable shared state across requests would require non-trivial scripting that is harder to read and maintain than a plain Spring Boot controller. They are well-suited for stateless contract mocking but not for this use case.

### Testcontainers + WireMock container
A WireMock Docker image launched via Testcontainers could stub `POST /process` responses for the duration of each integration test, with no extra deployed service.

Rejected: Testcontainers containers are lifecycle-bound to JVM test execution and cannot serve a persistently running Kubernetes cluster. Manual end-to-end flows (e.g. exercising the full Saga locally) would have no PSP to call, and `GET /balances` inspection during development would be unavailable. Testcontainers is the right tool for isolated unit/integration tests of `PspHttpAdapter` specifically, but it does not replace a cluster-level stub.

### Mocking the `BankingServicePort` in Payments
The port interface could be replaced by an in-process fake implementation via a Spring profile, removing the need for any HTTP hop.

Rejected: this would skip the HTTP adapter entirely (`PspHttpAdapter`), leaving that layer untested in local runs. A running stub validates the full integration path.

## Consequences
- The local cluster always includes one extra pod (`psp-stub`), adding a small resource overhead.
- `psp-stub` has no database; state is lost on pod restart, which is acceptable for a simulator.
- Any new PSP contract behaviour (e.g. rate limiting, partial approvals) must be added to `psp-stub` before it can be exercised locally.
