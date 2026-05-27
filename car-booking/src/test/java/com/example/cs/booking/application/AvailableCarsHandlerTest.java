package com.example.cs.booking.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.cs.booking.domain.Car;
import com.example.cs.booking.domain.CarRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AvailableCarsHandlerTest {

  private final CarRepository carRepository = mock(CarRepository.class);
  private final AvailableCarsHandler handler = new AvailableCarsHandler(carRepository);

  @Test
  void handle_returnsAvailableCars() {
    var car = Car.reconstitute(UUID.randomUUID(), "SEDAN");
    when(carRepository.findAvailable()).thenReturn(List.of(car));

    assertThat(handler.handle()).containsExactly(car);
  }

  @Test
  void handle_withNoCarsAvailable_returnsEmptyList() {
    when(carRepository.findAvailable()).thenReturn(List.of());

    assertThat(handler.handle()).isEmpty();
  }
}
