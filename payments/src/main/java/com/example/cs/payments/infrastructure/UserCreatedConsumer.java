package com.example.cs.payments.infrastructure;

import com.example.cs.common.UserCreated;
import com.example.cs.payments.application.CreateAccountCommand;
import com.example.cs.payments.application.CreateAccountUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserCreatedConsumer {

  private final CreateAccountUseCase createAccountUseCase;

  @RabbitListener(queues = RabbitMqConfig.USER_CREATED_QUEUE)
  void handle(UserCreated event) {
    createAccountUseCase.handle(new CreateAccountCommand(event.id(), event.bankAccount()));
  }
}
