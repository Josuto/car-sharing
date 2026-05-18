package com.example.cs.usermanagement.application;

import com.example.cs.usermanagement.domain.User;

public interface CreateUserUseCase {
    User handle(CreateUserCommand command);
}
