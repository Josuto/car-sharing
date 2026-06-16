package com.example.cs.usermanagement.infrastructure;

import com.example.cs.usermanagement.domain.UserEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class RabbitMqUserEventPublisher implements UserEventPublisher {

  static final String EXCHANGE = "user-events";

  private final RabbitTemplate rabbitTemplate;

  @Override
  public void publish(Object event) {
    rabbitTemplate.convertAndSend(EXCHANGE, event.getClass().getSimpleName(), event);
  }
}
