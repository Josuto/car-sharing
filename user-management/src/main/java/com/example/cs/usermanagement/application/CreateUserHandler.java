package com.example.cs.usermanagement.application;

import com.example.cs.common.UserCreated;
import com.example.cs.usermanagement.domain.User;
import com.example.cs.usermanagement.domain.UserEventPublisher;
import com.example.cs.usermanagement.domain.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class CreateUserHandler implements CreateUserUseCase {

    private final UserRepository repository;
    private final UserEventPublisher publisher;

    @Override
    public User handle(CreateUserCommand command) {
        var user = User.create(UUID.randomUUID(), command.username(), command.name(), command.surname());
        repository.save(user);
        publisher.publish(new UserCreated(user.id().toString(), user.username(), user.name(), user.surname()));
        return user;
    }
}
