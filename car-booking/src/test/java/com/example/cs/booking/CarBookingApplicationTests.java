package com.example.cs.booking;

import com.example.cs.booking.domain.CarRepository;
import com.example.cs.booking.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class CarBookingApplicationTests {

  @MockitoBean CarRepository carRepository;
  @MockitoBean UserRepository userRepository;

  @Test
  void contextLoads() {}
}
