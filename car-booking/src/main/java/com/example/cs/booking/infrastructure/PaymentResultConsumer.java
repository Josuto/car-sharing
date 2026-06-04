package com.example.cs.booking.infrastructure;

import com.example.cs.booking.application.PaymentResultCommand;
import com.example.cs.booking.application.PaymentResultUseCase;
import com.example.cs.common.PaymentProcessed;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class PaymentResultConsumer {

  private final PaymentResultUseCase paymentResultUseCase;

  @RabbitListener(queues = RabbitMqConfig.PAYMENT_PROCESSED_QUEUE)
  void consume(PaymentProcessed event) {
    paymentResultUseCase.handle(new PaymentResultCommand(event.bookingId(), event.success()));
  }
}
