package com.example.cs.payments.infrastructure;

import com.example.cs.common.UserBankAccountChanged;
import com.example.cs.payments.application.UpdateBankAccountCommand;
import com.example.cs.payments.application.UpdateBankAccountUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserBankAccountChangedConsumer {

  private final UpdateBankAccountUseCase updateBankAccountUseCase;

  @RabbitListener(queues = RabbitMqConfig.USER_BANK_ACCOUNT_CHANGED_QUEUE)
  void handle(UserBankAccountChanged event) {
    updateBankAccountUseCase.handle(
        new UpdateBankAccountCommand(event.userId(), event.bankAccount()));
  }
}
