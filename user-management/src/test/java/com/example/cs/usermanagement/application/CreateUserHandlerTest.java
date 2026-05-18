package com.example.cs.usermanagement.application;

import com.example.cs.common.UserCreated;
import com.example.cs.usermanagement.domain.UserEventPublisher;
import com.example.cs.usermanagement.domain.UserRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CreateUserHandlerTest {

    @Test
    void handle_savesUserAndPublishesUserCreatedEvent() {
        var repository = mock(UserRepository.class);
        var publisher = mock(UserEventPublisher.class);
        var handler = new CreateUserHandler(repository, publisher);

        var result = handler.handle(new CreateUserCommand("johndoe", "John", "Doe"));

        verify(repository).save(argThat(u ->
                u.username().equals("johndoe") && !u.isDebtor() && !u.isDeleted()));
        verify(publisher).publish(any(UserCreated.class));
        assertThat(result.username()).isEqualTo("johndoe");
    }
}
