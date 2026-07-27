package com.example.cs.payments.infrastructure;

import com.example.cs.payments.domain.Account;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
class AccountJpaEntity {

  @Id private String id;

  @Column(nullable = false)
  private String userId;

  @Column(nullable = false)
  private String bankAccount;

  static AccountJpaEntity from(Account account) {
    return new AccountJpaEntity(
        account.id().toString(), account.userId().toString(), account.bankAccount());
  }

  Account toDomain() {
    return Account.reconstitute(UUID.fromString(id), UUID.fromString(userId), bankAccount);
  }
}
