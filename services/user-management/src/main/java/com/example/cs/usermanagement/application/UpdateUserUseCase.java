package com.example.cs.usermanagement.application;

import com.example.cs.usermanagement.domain.User;

public interface UpdateUserUseCase {
  User handle(UpdateUserCommand command);
}
