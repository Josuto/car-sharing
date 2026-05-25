package com.example.cs.registry.domain;

public class DuplicateRegistrationNumberException extends RuntimeException {
  public DuplicateRegistrationNumberException(String registrationNumber) {
    super("Car with registration number " + registrationNumber + " already exists");
  }
}
