package com.example.cs.registry.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cs.common.CarRegistered;
import com.example.cs.registry.domain.CarEventPublisher;
import com.example.cs.registry.domain.CarRepository;
import com.example.cs.registry.domain.DuplicateRegistrationNumberException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RegisterCarHandlerTest {

  @Test
  void handle_savesCarAndPublishesCarRegisteredEvent() {
    var repository = mock(CarRepository.class);
    var publisher = mock(CarEventPublisher.class);
    var handler = new RegisterCarHandler(repository, publisher);
    var ownerId = UUID.randomUUID();

    var result = handler.handle(new RegisterCarCommand(ownerId, "SEDAN", "1234ABC"));

    verify(repository).save(argThat(c -> c.registrationNumber().equals("1234ABC")));
    verify(publisher).publish(any(CarRegistered.class));
    assertThat(result.registrationNumber()).isEqualTo("1234ABC");
  }

  @Test
  void handle_withDuplicateRegistrationNumber_throwsWithoutSavingOrPublishing() {
    var repository = mock(CarRepository.class);
    var publisher = mock(CarEventPublisher.class);
    when(repository.existsByRegistrationNumber("1234ABC")).thenReturn(true);
    var handler = new RegisterCarHandler(repository, publisher);

    assertThatThrownBy(
            () -> handler.handle(new RegisterCarCommand(UUID.randomUUID(), "SEDAN", "1234ABC")))
        .isInstanceOf(DuplicateRegistrationNumberException.class);

    verify(repository, never()).save(any());
    verify(publisher, never()).publish(any());
  }
}
