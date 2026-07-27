package com.example.cs.registry.infrastructure;

import com.example.cs.registry.application.RegisterCarCommand;
import com.example.cs.registry.application.RegisterCarUseCase;
import com.example.cs.registry.domain.Car;
import com.example.cs.registry.domain.DuplicateRegistrationNumberException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
class CarController {

  private final RegisterCarUseCase registerCarUseCase;

  @PostMapping
  ResponseEntity<CarResponse> register(@RequestBody RegisterCarRequest request) {
    var car =
        registerCarUseCase.handle(
            new RegisterCarCommand(
                request.ownerId(), request.type(), request.registrationNumber()));
    return ResponseEntity.status(HttpStatus.CREATED).body(CarResponse.from(car));
  }

  @ExceptionHandler({IllegalArgumentException.class, DuplicateRegistrationNumberException.class})
  ResponseEntity<Void> handleBadRequest() {
    return ResponseEntity.badRequest().build();
  }

  record RegisterCarRequest(UUID ownerId, String type, String registrationNumber) {}

  record CarResponse(String id, String ownerId, String type, String registrationNumber) {
    static CarResponse from(Car car) {
      return new CarResponse(
          car.id().toString(), car.ownerId().toString(), car.type(), car.registrationNumber());
    }
  }
}
