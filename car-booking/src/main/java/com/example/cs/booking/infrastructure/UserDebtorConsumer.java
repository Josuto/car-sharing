package com.example.cs.booking.infrastructure;

import com.example.cs.booking.domain.User;
import com.example.cs.booking.domain.UserRepository;
import com.example.cs.common.BorrowerFlaggedAsDebtor;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserDebtorConsumer {

  private final UserRepository userRepository;

  @RabbitListener(queues = "booking.borrower-flagged-as-debtor")
  void handle(BorrowerFlaggedAsDebtor event) {
    userRepository.save(User.reconstitute(UUID.fromString(event.userId()), true));
  }
}
