package com.example.cs.usermanagement.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
  void save(User user);

  Optional<User> findById(UUID id);

  Optional<User> findByUsername(String username);

  long countDebtors();
}
