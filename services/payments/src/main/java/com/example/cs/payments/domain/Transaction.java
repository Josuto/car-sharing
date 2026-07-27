package com.example.cs.payments.domain;

import java.util.UUID;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class Transaction {

  private final UUID id;
  private final UUID bookingId;
  private final UUID borrowerId;
  private final Money amount;
  private final TransactionStatus status;

  private Transaction(
      UUID id, UUID bookingId, UUID borrowerId, Money amount, TransactionStatus status) {
    this.id = id;
    this.bookingId = bookingId;
    this.borrowerId = borrowerId;
    this.amount = amount;
    this.status = status;
  }

  public static Transaction create(
      UUID bookingId, UUID borrowerId, Money amount, TransactionStatus status) {
    return new Transaction(UUID.randomUUID(), bookingId, borrowerId, amount, status);
  }

  public static Transaction reconstitute(
      UUID id, UUID bookingId, UUID borrowerId, Money amount, TransactionStatus status) {
    return new Transaction(id, bookingId, borrowerId, amount, status);
  }
}
