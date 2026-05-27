package com.example.cs.booking.infrastructure;

import com.example.cs.booking.domain.User;
import com.example.cs.booking.domain.UserRepository;
import com.example.cs.common.UserCreated;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserCreatedConsumer {

  private final UserRepository userRepository;

  @RabbitListener(queues = "booking.user-created")
  void handle(UserCreated event) {
    userRepository.save(User.reconstitute(UUID.fromString(event.id()), false));
  }
}
