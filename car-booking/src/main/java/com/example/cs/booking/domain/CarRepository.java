package com.example.cs.booking.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarRepository {
  List<Car> findAvailable();

  Optional<Car> findById(UUID id);

  void save(Car car);
}
