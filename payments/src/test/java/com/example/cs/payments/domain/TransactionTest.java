package com.example.cs.payments.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TransactionTest {

  @Test
  void create_storesAllFields() {
    var bookingId = UUID.randomUUID();
    var borrowerId = UUID.randomUUID();
    var amount = Money.ofEur(BigDecimal.valueOf(30));

    var transaction = Transaction.create(bookingId, borrowerId, amount, TransactionStatus.SUCCESS);

    assertThat(transaction.id()).isNotNull();
    assertThat(transaction.bookingId()).isEqualTo(bookingId);
    assertThat(transaction.borrowerId()).isEqualTo(borrowerId);
    assertThat(transaction.amount()).isEqualTo(amount);
    assertThat(transaction.status()).isEqualTo(TransactionStatus.SUCCESS);
  }
}
