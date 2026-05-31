# ADR-001: Bank account stored in User Management service

## Status
Accepted

## Date
2026-05-31

## Context
The Payments service needs each user's bank account number to instruct the PSP where to deduct
booking fees. The question is which service owns and exposes this data.

The ideal solution would be a dedicated, PCI-DSS-compliant service (or PSP tokenisation flow)
where users register payment methods directly with the PSP and only an opaque token is stored
in the application. However, this requires additional infrastructure and a separate user-facing
input flow that is out of scope for the MVP.

## Decision
Bank account is stored as an attribute of the `User` aggregate in User Management. It is required
at user creation and can be updated via `PUT /users/{id}` — the same endpoint already used to
manage other user details. No separate endpoint, service, or UI flow is needed.

Two events carry bank account information to Payments:

- `UserCreated` — includes `bankAccount` so Payments creates an account record on first signup.
- `UserBankAccountChanged` — emitted **only** when the bank account value actually changes.
  Payments is the sole consumer. No event is emitted for changes to other user fields (name,
  surname) because no downstream service needs them.

## Rationale
Piggybacking on the existing user management flow avoids building PSP tokenisation in an MVP.
Users already interact with User Management; adding `bankAccount` as a required field there
requires no new endpoints or UI.

## Known shortcoming — not the ideal solution
Storing bank account details alongside general user data conflates two bounded contexts with very
different compliance requirements. Raw account numbers are sensitive PII; in a production system
they must never sit in a general-purpose user table.

The correct design: users register a payment method via a PSP-hosted widget; the PSP returns an
opaque token; only the token is stored in Payments. This ADR accepts the simplified design solely
for MVP purposes and **must be revisited before this system handles real financial data**.

## Consequences
- `users` table in User Management gains a `bank_account` column (required, not nullable).
- `accounts` table in Payments stores the bank account number received from events, not a local
  balance — the PSP is the source of truth for user balances.
- Consumers of `UserCreated` must handle the new `bankAccount` field.
