package com.example.cs.booking.infrastructure;

import com.example.cs.booking.application.AvailableCarsHandler;
import com.example.cs.booking.application.AvailableCarsUseCase;
import com.example.cs.booking.application.CreateBookingHandler;
import com.example.cs.booking.application.CreateBookingUseCase;
import com.example.cs.booking.application.FlagLateReturnDebtorsHandler;
import com.example.cs.booking.application.PaymentResultHandler;
import com.example.cs.booking.application.PaymentResultUseCase;
import com.example.cs.booking.application.ReturnCarHandler;
import com.example.cs.booking.application.ReturnCarUseCase;
import com.example.cs.booking.domain.BookingEventPublisher;
import com.example.cs.booking.domain.BookingRepository;
import com.example.cs.booking.domain.CarRepository;
import com.example.cs.booking.domain.UserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CarBookingConfig {

  @Bean
  AvailableCarsUseCase availableCarsUseCase(
      CarRepository carRepository, MeterRegistry meterRegistry) {
    return new AvailableCarsHandler(carRepository, meterRegistry);
  }

  @Bean
  CreateBookingUseCase createBookingUseCase(
      CarRepository carRepository,
      UserRepository userRepository,
      BookingRepository bookingRepository,
      BookingEventPublisher bookingEventPublisher,
      MeterRegistry meterRegistry) {
    return new CreateBookingHandler(
        carRepository, userRepository, bookingRepository, bookingEventPublisher, meterRegistry);
  }

  @Bean
  PaymentResultUseCase paymentResultUseCase(
      BookingRepository bookingRepository, MeterRegistry meterRegistry) {
    return new PaymentResultHandler(bookingRepository, meterRegistry);
  }

  @Bean
  ReturnCarUseCase returnCarUseCase(BookingRepository bookingRepository) {
    return new ReturnCarHandler(bookingRepository);
  }

  @Bean
  FlagLateReturnDebtorsHandler flagLateReturnDebtorsHandler(
      BookingRepository bookingRepository,
      UserRepository userRepository,
      BookingEventPublisher bookingEventPublisher) {
    return new FlagLateReturnDebtorsHandler(
        bookingRepository, userRepository, bookingEventPublisher);
  }

  @Bean
  ActiveBookingMetrics activeBookingMetrics(BookingRepository bookingRepository) {
    return new ActiveBookingMetrics(bookingRepository);
  }
}
