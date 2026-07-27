package com.example.cs.usermanagement.application;

import java.util.UUID;

public interface DeleteUserUseCase {
  void handle(UUID id);
}
