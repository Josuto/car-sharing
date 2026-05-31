package com.example.cs.payments.application;

import com.example.cs.payments.domain.Account;
import com.example.cs.payments.domain.AccountRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateAccountHandler implements CreateAccountUseCase {

  private final AccountRepository repository;

  @Override
  public void handle(CreateAccountCommand command) {
    var userId = UUID.fromString(command.userId());
    if (repository.findByUserId(userId).isPresent()) {
      return;
    }
    repository.save(Account.create(userId, command.bankAccount()));
  }
}
