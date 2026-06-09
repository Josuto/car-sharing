package com.example.cs.usermanagement.infrastructure;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RabbitMqConfig {

  static final String BOOKING_EVENTS_EXCHANGE = "booking-events";
  static final String BORROWER_FLAGGED_QUEUE = "user-management.borrower-flagged-as-debtor";

  @Bean
  DirectExchange bookingEventsExchange() {
    return new DirectExchange(BOOKING_EVENTS_EXCHANGE);
  }

  @Bean
  Queue borrowerFlaggedQueue() {
    return new Queue(BORROWER_FLAGGED_QUEUE);
  }

  @Bean
  Binding borrowerFlaggedBinding() {
    return BindingBuilder.bind(borrowerFlaggedQueue())
        .to(bookingEventsExchange())
        .with("BorrowerFlaggedAsDebtor");
  }
}
