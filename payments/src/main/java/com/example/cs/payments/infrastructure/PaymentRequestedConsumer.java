package com.example.cs.payments.infrastructure;

import com.example.cs.common.BookingPaymentRequested;
import com.example.cs.payments.application.ProcessPaymentCommand;
import com.example.cs.payments.application.ProcessPaymentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class PaymentRequestedConsumer {

  private final ProcessPaymentUseCase processPaymentUseCase;

  @RabbitListener(queues = RabbitMqConfig.PAYMENT_REQUESTED_QUEUE)
  void handle(BookingPaymentRequested event) {
    processPaymentUseCase.handle(
        new ProcessPaymentCommand(
            event.bookingId(), event.borrowerId(), event.startDate(), event.endDate()));
  }
}
