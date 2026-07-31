# Gateway

## Contents

1. [Goal](#goal)
2. [Input ports](#input-ports)
3. [Output / side effects](#output--side-effects)
4. [Build / run / test](#build--run--test)
5. [Infrastructure](#infrastructure)
6. [Observability](#observability)

## Goal

Single inbound entry point for the car-sharing platform. All client HTTP traffic passes through the Gateway, which proxies requests to the appropriate downstream service. The Gateway contains no business logic; it is a pure routing layer built on Spring Cloud Gateway (Server MVC).

## Input ports

### Routing table

| Route | Methods | Upstream service |
|---|---|---|
| `/users`, `/users/**` | `POST`, `PUT`, `DELETE` | User Management `:8081` |
| `/cars` | `POST` | Car Registry `:8082` |
| `/cars`, `/bookings`, `/bookings/**` | all | Car Booking `:8083` |

Upstream addresses are resolved from `application-k8s.yaml` when running in the Kubernetes cluster (`user-management:8081`, `car-registry:8082`, `car-booking:8083`) and from `application.yaml` for local development (`localhost:808x`).

### AMQP consumers

None.

## Output / side effects

### AMQP events published

None.

### Database writes

None.

### External HTTP calls

None. The Gateway proxies inbound requests — it does not initiate outbound HTTP of its own.

## Build / run / test

```bash
# build + test this module only
./gradlew :gateway:build

# run locally (routes to localhost:808x)
./gradlew :gateway:bootRun

# run tests
./gradlew :gateway:test
```

## Infrastructure

Manifest: `infra/gateway.yaml`

| Resource | Kind | Details |
|---|---|---|
| `gateway` | Deployment | 1 replica; image `car-sharing/gateway:latest`; port 8080; env from `car-sharing-config` ConfigMap + `OTEL_SERVICE_NAME=gateway`, `SPRING_PROFILES_ACTIVE=k8s` |
| `gateway` | Service | `NodePort`; port 8080 — the only service exposed externally in the cluster |

## Observability

No custom Micrometer metrics. Default Spring Boot actuator endpoints (`/actuator/health`, `/actuator/info`) are available on port 8080.
