package com.example.cs.registry.infrastructure;

import com.example.cs.registry.domain.CarEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(RabbitTemplate.class)
@RequiredArgsConstructor
class RabbitMqCarEventPublisher implements CarEventPublisher {

  static final String EXCHANGE = "car-events";

  private final RabbitTemplate rabbitTemplate;

  @Override
  public void publish(Object event) {
    rabbitTemplate.convertAndSend(EXCHANGE, event.getClass().getSimpleName(), event);
  }
}
