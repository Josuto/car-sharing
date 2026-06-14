package com.example.cs.payments.infrastructure;

import com.example.cs.common.BookingPaymentRequested;
import com.example.cs.payments.application.ProcessPaymentCommand;
import com.example.cs.payments.application.ProcessPaymentUseCase;
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
class PaymentRequestedConsumer {

  private static final Logger log = LoggerFactory.getLogger(PaymentRequestedConsumer.class);

  private final ProcessPaymentUseCase processPaymentUseCase;

  @RabbitListener(queues = RabbitMqConfig.PAYMENT_REQUESTED_QUEUE)
  void handle(
      @Payload BookingPaymentRequested event,
      @Header(value = AmqpHeaders.REDELIVERED, defaultValue = "false") boolean redelivered) {
    if (redelivered) {
      log.warn("Redelivered message received: {}", event);
    }
    processPaymentUseCase.handle(
        new ProcessPaymentCommand(
            event.bookingId(), event.borrowerId(), event.startDate(), event.endDate()));
  }
}
