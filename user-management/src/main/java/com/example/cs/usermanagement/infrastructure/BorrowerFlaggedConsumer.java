package com.example.cs.usermanagement.infrastructure;

import com.example.cs.common.BorrowerFlaggedAsDebtor;
import com.example.cs.usermanagement.application.UpdateDebtorStatusCommand;
import com.example.cs.usermanagement.application.UpdateDebtorStatusUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class BorrowerFlaggedConsumer {

  private final UpdateDebtorStatusUseCase updateDebtorStatusUseCase;

  @RabbitListener(queues = RabbitMqConfig.BORROWER_FLAGGED_QUEUE)
  void consume(BorrowerFlaggedAsDebtor event) {
    updateDebtorStatusUseCase.handle(new UpdateDebtorStatusCommand(event.userId()));
  }
}
