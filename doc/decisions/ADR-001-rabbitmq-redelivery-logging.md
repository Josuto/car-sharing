# ADR-001: Explicit redelivery WARN logging in RabbitMQ consumers

## Status
Accepted

## Date
2026-06-14

## Context
RabbitMQ marks a message as redelivered (`amqp_redelivered=true`) when it was previously delivered but not acknowledged — typically because the consumer threw an uncaught exception or the connection was lost mid-processing. This is a signal that something went wrong on the first attempt and warrants a WARN log so operators can detect unexpected redelivery spikes.

The platform has 8 `@RabbitListener` consumers spread across `car-booking`, `payments`, and `user-management`. A strategy was needed for how to apply this cross-cutting concern without duplicating logic across every consumer.

## Decision
Each consumer declares a `@Header(value = AmqpHeaders.REDELIVERED, defaultValue = "false") boolean redelivered` parameter and checks it explicitly at the top of the method body, logging WARN if true. No AOP or container-level interception is used.

## Alternatives Considered

### Spring AOP `@Aspect`
A `@Aspect` could define a pointcut over all `@RabbitListener` methods and check the redelivery header.

Rejected: Spring AOP intercepts Spring bean proxies, not the raw AMQP delivery. The `redelivered` flag lives in `MessageProperties`, which is not available to the aspect without either keeping `@Header boolean redelivered` in every consumer signature anyway (eliminating the benefit) or accessing Spring AMQP's internal thread-local state (fragile, not part of the public API).

### AMQP container advice chain
`SimpleRabbitListenerContainerFactory.setAdviceChain(...)` accepts a `MethodInterceptor` that runs before message conversion, with access to the raw `Message` and its `MessageProperties.isRedelivered()`. A single interceptor registered in the container factory would cover all consumers with no per-consumer code.

Rejected for this project: the interceptor is invisible in consumer code. A newcomer reading any consumer sees no trace of the redelivery concern and must discover it in the container factory configuration. For an education project where source code is the primary documentation, this reduces discoverability disproportionately. The advice chain approach is the right choice at scale (many consumers), but the boilerplate cost across 8 consumers is acceptable here.

## Consequences
- Each consumer method carries a `boolean redelivered` parameter and a two-line guard — visible and self-explanatory to any reader.
- If the consumer count grows significantly, this decision should be revisited in favour of the container advice chain approach.
