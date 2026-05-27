package com.example.cs.booking.application;

import com.example.cs.booking.domain.Car;
import com.example.cs.booking.domain.CarRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AvailableCarsHandler implements AvailableCarsUseCase {

  private final CarRepository carRepository;

  @Override
  public List<Car> handle() {
    return carRepository.findAvailable();
  }
}
