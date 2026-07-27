package com.example.cs.payments.infrastructure;

import com.example.cs.common.UserBankAccountChanged;
import com.example.cs.payments.application.UpdateBankAccountCommand;
import com.example.cs.payments.application.UpdateBankAccountUseCase;
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
class UserBankAccountChangedConsumer {

  private static final Logger log = LoggerFactory.getLogger(UserBankAccountChangedConsumer.class);

  private final UpdateBankAccountUseCase updateBankAccountUseCase;

  @RabbitListener(queues = RabbitMqConfig.USER_BANK_ACCOUNT_CHANGED_QUEUE)
  void handle(
      @Payload UserBankAccountChanged event,
      @Header(value = AmqpHeaders.REDELIVERED, defaultValue = "false") boolean redelivered) {
    if (redelivered) {
      log.warn("Redelivered message received: {}", event);
    }
    updateBankAccountUseCase.handle(
        new UpdateBankAccountCommand(event.userId(), event.bankAccount()));
  }
}
