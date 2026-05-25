package com.example.cs.registry.application;

import com.example.cs.registry.domain.Car;
import com.example.cs.registry.domain.CarEventPublisher;
import com.example.cs.registry.domain.CarRepository;
import com.example.cs.registry.domain.DuplicateRegistrationNumberException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegisterCarHandler implements RegisterCarUseCase {

  private final CarRepository repository;
  private final CarEventPublisher publisher;

  @Override
  public Car handle(RegisterCarCommand command) {
    if (repository.existsByRegistrationNumber(command.registrationNumber()))
      throw new DuplicateRegistrationNumberException(command.registrationNumber());
    var car =
        Car.create(
            UUID.randomUUID(), command.ownerId(), command.type(), command.registrationNumber());
    repository.save(car);
    car.pullDomainEvents().forEach(publisher::publish);
    return car;
  }
}
