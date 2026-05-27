package com.example.cs.booking.application;

import com.example.cs.booking.domain.Car;
import java.util.List;

public interface AvailableCarsUseCase {
  List<Car> handle();
}
