package com.example.cs.booking.infrastructure;

import com.example.cs.booking.application.AvailableCarsUseCase;
import com.example.cs.booking.domain.Car;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
class CarsController {

  private final AvailableCarsUseCase availableCarsUseCase;

  @GetMapping
  List<CarResponse> getCars() {
    return availableCarsUseCase.handle().stream().map(CarResponse::from).toList();
  }

  record CarResponse(String id, String type) {
    static CarResponse from(Car car) {
      return new CarResponse(car.id().toString(), car.type());
    }
  }
}
