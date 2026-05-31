package com.example.cs.payments.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class AccountTest {

  @Test
  void create_storeUserIdAndBankAccount() {
    var userId = UUID.randomUUID();

    var account = Account.create(userId, "ES1234567890");

    assertThat(account.userId()).isEqualTo(userId);
    assertThat(account.bankAccount()).isEqualTo("ES1234567890");
    assertThat(account.id()).isNotNull();
  }

  @Test
  void updateBankAccount_changesStoredBankAccount() {
    var account = Account.create(UUID.randomUUID(), "ES1234567890");

    account.updateBankAccount("ES0987654321");

    assertThat(account.bankAccount()).isEqualTo("ES0987654321");
  }
}
