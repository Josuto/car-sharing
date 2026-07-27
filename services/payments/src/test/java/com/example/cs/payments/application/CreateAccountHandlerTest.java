package com.example.cs.payments.application;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cs.payments.domain.Account;
import com.example.cs.payments.domain.AccountRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CreateAccountHandlerTest {

  @Test
  void handle_savesAccountForNewUser() {
    var repository = mock(AccountRepository.class);
    var userId = UUID.randomUUID();
    when(repository.findByUserId(userId)).thenReturn(Optional.empty());
    var handler = new CreateAccountHandler(repository);

    handler.handle(new CreateAccountCommand(userId.toString(), "ES1234567890"));

    verify(repository)
        .save(
            argThat(
                account ->
                    account.userId().equals(userId)
                        && account.bankAccount().equals("ES1234567890")));
  }

  @Test
  void handle_doesNothingIfAccountAlreadyExists() {
    var repository = mock(AccountRepository.class);
    var userId = UUID.randomUUID();
    when(repository.findByUserId(userId))
        .thenReturn(Optional.of(Account.create(userId, "ES1234567890")));
    var handler = new CreateAccountHandler(repository);

    handler.handle(new CreateAccountCommand(userId.toString(), "ES1234567890"));

    verify(repository, never()).save(argThat(account -> true));
  }
}
