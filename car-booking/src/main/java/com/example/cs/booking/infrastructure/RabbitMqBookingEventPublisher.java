package com.example.cs.booking.infrastructure;

import com.example.cs.booking.domain.BookingEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(RabbitTemplate.class)
@RequiredArgsConstructor
class RabbitMqBookingEventPublisher implements BookingEventPublisher {

  static final String EXCHANGE = "booking-events";

  private final RabbitTemplate rabbitTemplate;

  @Override
  public void publish(Object event) {
    rabbitTemplate.convertAndSend(EXCHANGE, event.getClass().getSimpleName(), event);
  }
}
