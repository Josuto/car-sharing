package com.example.cs.booking.infrastructure;

import com.example.cs.booking.domain.Car;
import com.example.cs.booking.domain.CarRepository;
import com.example.cs.common.CarRegistered;
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
class CarRegisteredConsumer {

  private static final Logger log = LoggerFactory.getLogger(CarRegisteredConsumer.class);

  private final CarRepository carRepository;

  @RabbitListener(queues = "booking.car-registered")
  void handle(
      @Payload CarRegistered event,
      @Header(value = AmqpHeaders.REDELIVERED, defaultValue = "false") boolean redelivered) {
    if (redelivered) {
      log.warn("Redelivered message received: {}", event);
    }
    carRepository.save(Car.reconstitute(UUID.fromString(event.id()), event.type()));
  }
}
