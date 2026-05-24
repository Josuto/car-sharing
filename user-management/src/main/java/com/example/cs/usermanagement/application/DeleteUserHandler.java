package com.example.cs.usermanagement.application;

import com.example.cs.usermanagement.domain.UserNotFoundException;
import com.example.cs.usermanagement.domain.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteUserHandler implements DeleteUserUseCase {

  private final UserRepository repository;

  @Override
  public void handle(UUID id) {
    var user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    user.delete();
    repository.save(user);
  }
}
