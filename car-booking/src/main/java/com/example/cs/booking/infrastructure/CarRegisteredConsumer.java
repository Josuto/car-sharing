package com.example.cs.booking.infrastructure;

import com.example.cs.booking.domain.Car;
import com.example.cs.booking.domain.CarRepository;
import com.example.cs.common.CarRegistered;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class CarRegisteredConsumer {

  private final CarRepository carRepository;

  @RabbitListener(queues = "booking.car-registered")
  void handle(CarRegistered event) {
    carRepository.save(Car.reconstitute(UUID.fromString(event.id()), event.type()));
  }
}
