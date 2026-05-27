package com.example.cs.booking.infrastructure;

import com.example.cs.booking.application.AvailableCarsHandler;
import com.example.cs.booking.application.AvailableCarsUseCase;
import com.example.cs.booking.domain.CarRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CarBookingConfig {

  @Bean
  AvailableCarsUseCase availableCarsUseCase(CarRepository carRepository) {
    return new AvailableCarsHandler(carRepository);
  }
}
