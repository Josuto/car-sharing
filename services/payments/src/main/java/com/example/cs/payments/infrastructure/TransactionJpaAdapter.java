package com.example.cs.payments.infrastructure;

import com.example.cs.payments.domain.Transaction;
import com.example.cs.payments.domain.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class TransactionJpaAdapter implements TransactionRepository {

  private final TransactionJpaRepository jpaRepository;

  @Override
  public void save(Transaction transaction) {
    jpaRepository.save(TransactionJpaEntity.from(transaction));
  }
}
