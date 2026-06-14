package com.example.cs.booking.application;

import com.example.cs.booking.domain.Car;
import com.example.cs.booking.domain.CarRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;

public class AvailableCarsHandler implements AvailableCarsUseCase {

  private final CarRepository carRepository;

  public AvailableCarsHandler(CarRepository carRepository, MeterRegistry meterRegistry) {
    this.carRepository = carRepository;
    Gauge.builder("cars.available", carRepository, r -> r.findAvailable().size())
        .register(meterRegistry);
  }

  @Override
  public List<Car> handle() {
    return carRepository.findAvailable();
  }
}
