package com.example.cs.payments.infrastructure;

import com.example.cs.payments.domain.Money;
import com.example.cs.payments.domain.Transaction;
import com.example.cs.payments.domain.TransactionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transactions")
@Getter
@NoArgsConstructor
@AllArgsConstructor
class TransactionJpaEntity {

  @Id private String id;

  @Column(nullable = false)
  private String bookingId;

  @Column(nullable = false)
  private String borrowerId;

  @Column(nullable = false)
  private BigDecimal amount;

  @Column(nullable = false)
  private String currency;

  @Column(nullable = false)
  private String status;

  static TransactionJpaEntity from(Transaction transaction) {
    return new TransactionJpaEntity(
        transaction.id().toString(),
        transaction.bookingId().toString(),
        transaction.borrowerId().toString(),
        transaction.amount().amount(),
        transaction.amount().currency(),
        transaction.status().name());
  }

  Transaction toDomain() {
    return Transaction.reconstitute(
        UUID.fromString(id),
        UUID.fromString(bookingId),
        UUID.fromString(borrowerId),
        new Money(amount, currency),
        TransactionStatus.valueOf(status));
  }
}
