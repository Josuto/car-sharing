package com.example.cs.booking;

import com.example.cs.booking.domain.BookingEventPublisher;
import com.example.cs.booking.domain.BookingRepository;
import com.example.cs.booking.domain.CarRepository;
import com.example.cs.booking.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class CarBookingApplicationTests {

  @TestConfiguration
  static class TestConfig {
    @Bean
    @Primary
    BookingEventPublisher noOpBookingEventPublisher() {
      return event -> {};
    }
  }

  @MockitoBean CarRepository carRepository;
  @MockitoBean UserRepository userRepository;
  @MockitoBean BookingRepository bookingRepository;

  @Test
  void contextLoads() {}
}
