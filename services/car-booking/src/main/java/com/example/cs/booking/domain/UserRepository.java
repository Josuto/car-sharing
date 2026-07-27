package com.example.cs.booking.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
  Optional<User> findById(UUID id);

  void save(User user);
}
