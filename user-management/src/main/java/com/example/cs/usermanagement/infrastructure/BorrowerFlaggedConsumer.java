package com.example.cs.usermanagement.infrastructure;

import com.example.cs.common.BorrowerFlaggedAsDebtor;
import com.example.cs.usermanagement.application.UpdateDebtorStatusCommand;
import com.example.cs.usermanagement.application.UpdateDebtorStatusUseCase;
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
class BorrowerFlaggedConsumer {

  private static final Logger log = LoggerFactory.getLogger(BorrowerFlaggedConsumer.class);

  private final UpdateDebtorStatusUseCase updateDebtorStatusUseCase;

  @RabbitListener(queues = RabbitMqConfig.BORROWER_FLAGGED_QUEUE)
  void consume(
      @Payload BorrowerFlaggedAsDebtor event,
      @Header(value = AmqpHeaders.REDELIVERED, defaultValue = "false") boolean redelivered) {
    if (redelivered) {
      log.warn("Redelivered message received: {}", event);
    }
    updateDebtorStatusUseCase.handle(new UpdateDebtorStatusCommand(event.userId()));
  }
}
