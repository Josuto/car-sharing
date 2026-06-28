package com.example.cs.payments.infrastructure;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RabbitMqConfig {

  static final String PAYMENT_EVENTS_EXCHANGE = "payment-events";
  static final String USER_EVENTS_EXCHANGE = "user-events";
  static final String BOOKING_EVENTS_EXCHANGE = "booking-events";

  static final String USER_CREATED_QUEUE = "payments.user-created";
  static final String USER_BANK_ACCOUNT_CHANGED_QUEUE = "payments.user-bank-account-changed";
  static final String PAYMENT_REQUESTED_QUEUE = "payments.booking-payment-requested";

  @Bean
  MessageConverter jsonMessageConverter() {
    return new JacksonJsonMessageConverter();
  }

  @Bean
  DirectExchange paymentEventsExchange() {
    return new DirectExchange(PAYMENT_EVENTS_EXCHANGE);
  }

  @Bean
  DirectExchange userEventsExchange() {
    return new DirectExchange(USER_EVENTS_EXCHANGE);
  }

  @Bean
  DirectExchange bookingEventsExchange() {
    return new DirectExchange(BOOKING_EVENTS_EXCHANGE);
  }

  @Bean
  Queue userCreatedQueue() {
    return new Queue(USER_CREATED_QUEUE);
  }

  @Bean
  Queue userBankAccountChangedQueue() {
    return new Queue(USER_BANK_ACCOUNT_CHANGED_QUEUE);
  }

  @Bean
  Queue paymentRequestedQueue() {
    return new Queue(PAYMENT_REQUESTED_QUEUE);
  }

  @Bean
  Binding userCreatedBinding() {
    return BindingBuilder.bind(userCreatedQueue()).to(userEventsExchange()).with("UserCreated");
  }

  @Bean
  Binding userBankAccountChangedBinding() {
    return BindingBuilder.bind(userBankAccountChangedQueue())
        .to(userEventsExchange())
        .with("UserBankAccountChanged");
  }

  @Bean
  Binding paymentRequestedBinding() {
    return BindingBuilder.bind(paymentRequestedQueue())
        .to(bookingEventsExchange())
        .with("BookingPaymentRequested");
  }
}
