package com.example.cs.usermanagement.domain;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(UUID id) {
    super("User not found: " + id);
  }
}
