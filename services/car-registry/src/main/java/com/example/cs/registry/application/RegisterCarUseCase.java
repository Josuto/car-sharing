package com.example.cs.registry.application;

import com.example.cs.registry.domain.Car;

public interface RegisterCarUseCase {
  Car handle(RegisterCarCommand command);
}
