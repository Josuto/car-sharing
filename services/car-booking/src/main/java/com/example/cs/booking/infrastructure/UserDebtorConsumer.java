package com.example.cs.booking.infrastructure;

import com.example.cs.booking.domain.User;
import com.example.cs.booking.domain.UserRepository;
import com.example.cs.common.BorrowerFlaggedAsDebtor;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserDebtorConsumer {

  private static final Logger log = LoggerFactory.getLogger(UserDebtorConsumer.class);

  private final UserRepository userRepository;

  @RabbitListener(queues = "booking.borrower-flagged-as-debtor")
  void handle(
      @Payload BorrowerFlaggedAsDebtor event,
      @Header(value = AmqpHeaders.REDELIVERED, defaultValue = "false") boolean redelivered) {
    if (redelivered) {
      log.warn("Redelivered message received: {}", event);
    }
    userRepository.save(User.reconstitute(UUID.fromString(event.userId()), true));
  }
}
