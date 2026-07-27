package com.example.cs.payments.domain;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
  void save(Account account);

  Optional<Account> findByUserId(UUID userId);
}
