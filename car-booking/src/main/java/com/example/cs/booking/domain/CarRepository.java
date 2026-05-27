package com.example.cs.booking.domain;

import java.util.List;

public interface CarRepository {
  List<Car> findAvailable();

  void save(Car car);
}
