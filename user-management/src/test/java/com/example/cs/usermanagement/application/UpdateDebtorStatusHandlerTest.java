package com.example.cs.usermanagement.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cs.usermanagement.domain.User;
import com.example.cs.usermanagement.domain.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UpdateDebtorStatusHandlerTest {

  private final UserRepository userRepository = mock(UserRepository.class);
  private final UpdateDebtorStatusHandler handler = new UpdateDebtorStatusHandler(userRepository);

  private final UUID userId = UUID.randomUUID();

  @Test
  void handle_withNonDebtorUser_savesUserAsDebtor() {
    var user = User.reconstitute(userId, "user1", "John", "Doe", "ACC-001", false, false);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    handler.handle(new UpdateDebtorStatusCommand(userId.toString()));

    verify(userRepository).save(argThat(User::isDebtor));
  }

  @Test
  void handle_withAlreadyDebtorUser_doesNothing() {
    var user = User.reconstitute(userId, "user1", "John", "Doe", "ACC-001", true, false);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    handler.handle(new UpdateDebtorStatusCommand(userId.toString()));

    verify(userRepository, never()).save(any());
  }

  @Test
  void handle_withUnknownUser_skipsWithoutThrowing() {
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    handler.handle(new UpdateDebtorStatusCommand(userId.toString()));

    verify(userRepository, never()).save(any());
  }
}
