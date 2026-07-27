package com.example.cs.booking.infrastructure;

import com.example.cs.booking.application.PaymentResultCommand;
import com.example.cs.booking.application.PaymentResultUseCase;
import com.example.cs.common.PaymentProcessed;
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
class PaymentResultConsumer {

  private static final Logger log = LoggerFactory.getLogger(PaymentResultConsumer.class);

  private final PaymentResultUseCase paymentResultUseCase;

  @RabbitListener(queues = RabbitMqConfig.PAYMENT_PROCESSED_QUEUE)
  void consume(
      @Payload PaymentProcessed event,
      @Header(value = AmqpHeaders.REDELIVERED, defaultValue = "false") boolean redelivered) {
    if (redelivered) {
      log.warn("Redelivered message received: {}", event);
    }
    paymentResultUseCase.handle(new PaymentResultCommand(event.bookingId(), event.success()));
  }
}
