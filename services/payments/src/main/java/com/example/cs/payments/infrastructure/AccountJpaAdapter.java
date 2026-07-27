package com.example.cs.payments.infrastructure;

import com.example.cs.payments.domain.Account;
import com.example.cs.payments.domain.AccountRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class AccountJpaAdapter implements AccountRepository {

  private final AccountJpaRepository jpaRepository;

  @Override
  public void save(Account account) {
    jpaRepository.save(AccountJpaEntity.from(account));
  }

  @Override
  public Optional<Account> findByUserId(UUID userId) {
    return jpaRepository.findByUserId(userId.toString()).map(AccountJpaEntity::toDomain);
  }
}
