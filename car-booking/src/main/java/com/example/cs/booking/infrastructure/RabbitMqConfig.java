package com.example.cs.booking.infrastructure;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RabbitMqConfig {

  static final String CAR_EVENTS_EXCHANGE = "car-events";
  static final String USER_EVENTS_EXCHANGE = "user-events";
  static final String PAYMENT_EVENTS_EXCHANGE = "payment-events";

  static final String CAR_REGISTERED_QUEUE = "booking.car-registered";
  static final String USER_CREATED_QUEUE = "booking.user-created";
  static final String BORROWER_FLAGGED_AS_DEBTOR_QUEUE = "booking.borrower-flagged-as-debtor";
  static final String PAYMENT_PROCESSED_QUEUE = "booking.payment-processed";

  @Bean
  DirectExchange carEventsExchange() {
    return new DirectExchange(CAR_EVENTS_EXCHANGE);
  }

  @Bean
  DirectExchange userEventsExchange() {
    return new DirectExchange(USER_EVENTS_EXCHANGE);
  }

  @Bean
  DirectExchange paymentEventsExchange() {
    return new DirectExchange(PAYMENT_EVENTS_EXCHANGE);
  }

  @Bean
  Queue carRegisteredQueue() {
    return new Queue(CAR_REGISTERED_QUEUE);
  }

  @Bean
  Queue userCreatedQueue() {
    return new Queue(USER_CREATED_QUEUE);
  }

  @Bean
  Queue borrowerFlaggedAsDebtorQueue() {
    return new Queue(BORROWER_FLAGGED_AS_DEBTOR_QUEUE);
  }

  @Bean
  Queue paymentProcessedQueue() {
    return new Queue(PAYMENT_PROCESSED_QUEUE);
  }

  @Bean
  Binding carRegisteredBinding() {
    return BindingBuilder.bind(carRegisteredQueue()).to(carEventsExchange()).with("CarRegistered");
  }

  @Bean
  Binding userCreatedBinding() {
    return BindingBuilder.bind(userCreatedQueue()).to(userEventsExchange()).with("UserCreated");
  }

  @Bean
  Binding borrowerFlaggedAsDebtorBinding() {
    return BindingBuilder.bind(borrowerFlaggedAsDebtorQueue())
        .to(userEventsExchange())
        .with("BorrowerFlaggedAsDebtor");
  }

  @Bean
  Binding paymentProcessedBinding() {
    return BindingBuilder.bind(paymentProcessedQueue())
        .to(paymentEventsExchange())
        .with("PaymentProcessed");
  }
}
