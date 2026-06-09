package com.example.cs.usermanagement.infrastructure;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.cs.common.BorrowerFlaggedAsDebtor;
import com.example.cs.usermanagement.application.UpdateDebtorStatusCommand;
import com.example.cs.usermanagement.application.UpdateDebtorStatusUseCase;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BorrowerFlaggedConsumerTest {

  private final UpdateDebtorStatusUseCase updateDebtorStatusUseCase =
      mock(UpdateDebtorStatusUseCase.class);
  private final BorrowerFlaggedConsumer consumer =
      new BorrowerFlaggedConsumer(updateDebtorStatusUseCase);

  @Test
  void consume_delegatesToUpdateDebtorStatusUseCase() {
    var userId = UUID.randomUUID().toString();
    var event = new BorrowerFlaggedAsDebtor(userId);

    consumer.consume(event);

    verify(updateDebtorStatusUseCase).handle(new UpdateDebtorStatusCommand(userId));
  }
}
