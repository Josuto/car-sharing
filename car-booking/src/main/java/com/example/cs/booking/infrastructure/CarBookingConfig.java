package com.example.cs.booking.infrastructure;

import com.example.cs.booking.application.AvailableCarsHandler;
import com.example.cs.booking.application.AvailableCarsUseCase;
import com.example.cs.booking.application.CreateBookingHandler;
import com.example.cs.booking.application.CreateBookingUseCase;
import com.example.cs.booking.application.PaymentResultHandler;
import com.example.cs.booking.application.PaymentResultUseCase;
import com.example.cs.booking.domain.BookingEventPublisher;
import com.example.cs.booking.domain.BookingRepository;
import com.example.cs.booking.domain.CarRepository;
import com.example.cs.booking.domain.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CarBookingConfig {

  @Bean
  AvailableCarsUseCase availableCarsUseCase(CarRepository carRepository) {
    return new AvailableCarsHandler(carRepository);
  }

  @Bean
  CreateBookingUseCase createBookingUseCase(
      CarRepository carRepository,
      UserRepository userRepository,
      BookingRepository bookingRepository,
      BookingEventPublisher bookingEventPublisher) {
    return new CreateBookingHandler(
        carRepository, userRepository, bookingRepository, bookingEventPublisher);
  }

  @Bean
  PaymentResultUseCase paymentResultUseCase(BookingRepository bookingRepository) {
    return new PaymentResultHandler(bookingRepository);
  }
}
