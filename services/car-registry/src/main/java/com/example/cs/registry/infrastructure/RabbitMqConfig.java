package com.example.cs.registry.infrastructure;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RabbitMqConfig {

  static final String CAR_EVENTS_EXCHANGE = "car-events";

  @Bean
  MessageConverter jsonMessageConverter() {
    return new JacksonJsonMessageConverter();
  }

  @Bean
  DirectExchange carEventsExchange() {
    return new DirectExchange(CAR_EVENTS_EXCHANGE);
  }
}
