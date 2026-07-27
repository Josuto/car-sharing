package com.example.cs.payments.domain;

import java.util.UUID;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class Account {

  private final UUID id;
  private final UUID userId;
  private String bankAccount;

  private Account(UUID id, UUID userId, String bankAccount) {
    this.id = id;
    this.userId = userId;
    this.bankAccount = bankAccount;
  }

  public static Account create(UUID userId, String bankAccount) {
    return new Account(UUID.randomUUID(), userId, bankAccount);
  }

  public static Account reconstitute(UUID id, UUID userId, String bankAccount) {
    return new Account(id, userId, bankAccount);
  }

  public void updateBankAccount(String bankAccount) {
    this.bankAccount = bankAccount;
  }
}
