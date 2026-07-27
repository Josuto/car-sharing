package com.example.cs.usermanagement.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cs.common.UserBankAccountChanged;
import com.example.cs.usermanagement.domain.User;
import com.example.cs.usermanagement.domain.UserEventPublisher;
import com.example.cs.usermanagement.domain.UserNotFoundException;
import com.example.cs.usermanagement.domain.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UpdateUserHandlerTest {

  @Test
  void handle_withNewBankAccount_savesUserAndPublishesUserBankAccountChangedEvent() {
    var id = UUID.randomUUID();
    var user = User.reconstitute(id, "johndoe", "John", "Doe", "ES1234567890", false, false);
    var repository = mock(UserRepository.class);
    var publisher = mock(UserEventPublisher.class);
    when(repository.findById(id)).thenReturn(Optional.of(user));
    var handler = new UpdateUserHandler(repository, publisher);

    var result = handler.handle(new UpdateUserCommand(id, "John", "Doe", "ES0987654321"));

    verify(repository).save(argThat(u -> u.bankAccount().equals("ES0987654321")));
    verify(publisher).publish(any(UserBankAccountChanged.class));
    assertThat(result.bankAccount()).isEqualTo("ES0987654321");
  }

  @Test
  void handle_withSameBankAccount_savesUserAndPublishesNoEvent() {
    var id = UUID.randomUUID();
    var user = User.reconstitute(id, "johndoe", "John", "Doe", "ES1234567890", false, false);
    var repository = mock(UserRepository.class);
    var publisher = mock(UserEventPublisher.class);
    when(repository.findById(id)).thenReturn(Optional.of(user));
    var handler = new UpdateUserHandler(repository, publisher);

    handler.handle(new UpdateUserCommand(id, "Jane", "Smith", "ES1234567890"));

    verify(repository).save(argThat(u -> u.name().equals("Jane") && u.surname().equals("Smith")));
    verify(publisher, never()).publish(any());
  }

  @Test
  void handle_throwsWhenUserNotFound() {
    var id = UUID.randomUUID();
    var repository = mock(UserRepository.class);
    when(repository.findById(id)).thenReturn(Optional.empty());
    var handler = new UpdateUserHandler(repository, mock(UserEventPublisher.class));

    assertThatExceptionOfType(UserNotFoundException.class)
        .isThrownBy(() -> handler.handle(new UpdateUserCommand(id, "John", "Doe", "ES1234567890")));
  }
}
