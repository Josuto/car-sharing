package com.example.cs.usermanagement.application;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cs.usermanagement.domain.User;
import com.example.cs.usermanagement.domain.UserNotFoundException;
import com.example.cs.usermanagement.domain.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DeleteUserHandlerTest {

  @Test
  void handle_softDeletesExistingUser() {
    var user = User.create(UUID.randomUUID(), "johndoe", "John", "Doe", "ES1234567890");
    var repository = mock(UserRepository.class);
    when(repository.findById(user.id())).thenReturn(Optional.of(user));
    var handler = new DeleteUserHandler(repository);

    handler.handle(user.id());

    verify(repository).save(argThat(u -> u.isDeleted()));
  }

  @Test
  void handle_throwsWhenUserNotFound() {
    var repository = mock(UserRepository.class);
    var id = UUID.randomUUID();
    when(repository.findById(id)).thenReturn(Optional.empty());
    var handler = new DeleteUserHandler(repository);

    assertThatExceptionOfType(UserNotFoundException.class).isThrownBy(() -> handler.handle(id));
  }
}
