package com.example.cs.payments.infrastructure;

import com.example.cs.common.UserCreated;
import com.example.cs.payments.application.CreateAccountCommand;
import com.example.cs.payments.application.CreateAccountUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserCreatedConsumer {

  private static final Logger log = LoggerFactory.getLogger(UserCreatedConsumer.class);

  private final CreateAccountUseCase createAccountUseCase;

  @RabbitListener(queues = RabbitMqConfig.USER_CREATED_QUEUE)
  void handle(
      @Payload UserCreated event,
      @Header(value = AmqpHeaders.REDELIVERED, defaultValue = "false") boolean redelivered) {
    if (redelivered) {
      log.warn("Redelivered message received: {}", event);
    }
    createAccountUseCase.handle(new CreateAccountCommand(event.id(), event.bankAccount()));
  }
}
