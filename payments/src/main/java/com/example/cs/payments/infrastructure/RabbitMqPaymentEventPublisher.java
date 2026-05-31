package com.example.cs.payments.infrastructure;

import com.example.cs.payments.domain.PaymentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(RabbitTemplate.class)
@RequiredArgsConstructor
class RabbitMqPaymentEventPublisher implements PaymentEventPublisher {

  static final String EXCHANGE = "payment-events";

  private final RabbitTemplate rabbitTemplate;

  @Override
  public void publish(Object event) {
    rabbitTemplate.convertAndSend(EXCHANGE, event.getClass().getSimpleName(), event);
  }
}
