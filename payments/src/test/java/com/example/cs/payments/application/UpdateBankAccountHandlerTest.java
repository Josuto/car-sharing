package com.example.cs.payments.application;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cs.payments.domain.Account;
import com.example.cs.payments.domain.AccountRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UpdateBankAccountHandlerTest {

  @Test
  void handle_updatesStoredBankAccount() {
    var repository = mock(AccountRepository.class);
    var userId = UUID.randomUUID();
    var account = Account.create(userId, "ES1234567890");
    when(repository.findByUserId(userId)).thenReturn(Optional.of(account));
    var handler = new UpdateBankAccountHandler(repository);

    handler.handle(new UpdateBankAccountCommand(userId.toString(), "ES0987654321"));

    verify(repository)
        .save(argThat(updatedAccount -> updatedAccount.bankAccount().equals("ES0987654321")));
  }
}
