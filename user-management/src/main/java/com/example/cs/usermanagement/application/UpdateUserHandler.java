package com.example.cs.usermanagement.application;

import com.example.cs.usermanagement.domain.User;
import com.example.cs.usermanagement.domain.UserEventPublisher;
import com.example.cs.usermanagement.domain.UserNotFoundException;
import com.example.cs.usermanagement.domain.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateUserHandler implements UpdateUserUseCase {

  private final UserRepository repository;
  private final UserEventPublisher publisher;

  @Override
  public User handle(UpdateUserCommand command) {
    var user =
        repository
            .findById(command.id())
            .orElseThrow(() -> new UserNotFoundException(command.id()));
    user.update(command.name(), command.surname(), command.bankAccount());
    repository.save(user);
    user.pullDomainEvents().forEach(publisher::publish);
    return user;
  }
}
