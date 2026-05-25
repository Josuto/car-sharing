package com.example.cs.registry.domain;

public interface CarRepository {
  void save(Car car);

  boolean existsByRegistrationNumber(String registrationNumber);
}
