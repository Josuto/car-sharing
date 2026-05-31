package com.example.cs.payments.application;

import com.example.cs.payments.domain.AccountRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateBankAccountHandler implements UpdateBankAccountUseCase {

  private final AccountRepository repository;

  @Override
  public void handle(UpdateBankAccountCommand command) {
    var userId = UUID.fromString(command.userId());
    repository
        .findByUserId(userId)
        .ifPresent(
            account -> {
              account.updateBankAccount(command.bankAccount());
              repository.save(account);
            });
  }
}
