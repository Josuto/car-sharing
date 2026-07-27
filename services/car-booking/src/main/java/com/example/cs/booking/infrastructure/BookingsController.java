package com.example.cs.booking.infrastructure;

import com.example.cs.booking.application.CreateBookingCommand;
import com.example.cs.booking.application.CreateBookingUseCase;
import com.example.cs.booking.application.ReturnCarCommand;
import com.example.cs.booking.application.ReturnCarUseCase;
import com.example.cs.booking.domain.Booking;
import com.example.cs.booking.domain.BookingNotFoundException;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
class BookingsController {

  private final CreateBookingUseCase createBookingUseCase;
  private final ReturnCarUseCase returnCarUseCase;

  @PostMapping
  ResponseEntity<?> createBooking(@RequestBody CreateBookingRequest request) {
    try {
      var booking =
          createBookingUseCase.handle(
              new CreateBookingCommand(
                  request.carId(), request.borrowerId(), request.startDate(), request.endDate()));
      return ResponseEntity.status(HttpStatus.CREATED).body(BookingResponse.from(booking));
    } catch (IllegalArgumentException exception) {
      return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }
  }

  @PutMapping("/{id}")
  ResponseEntity<?> returnCar(@PathVariable UUID id) {
    try {
      var booking = returnCarUseCase.handle(new ReturnCarCommand(id));
      return ResponseEntity.ok(BookingResponse.from(booking));
    } catch (BookingNotFoundException exception) {
      return ResponseEntity.notFound().build();
    } catch (IllegalArgumentException exception) {
      return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }
  }

  record CreateBookingRequest(
      UUID carId, UUID borrowerId, LocalDate startDate, LocalDate endDate) {}

  record BookingResponse(
      String id,
      String carId,
      String borrowerId,
      LocalDate startDate,
      LocalDate endDate,
      String status) {
    static BookingResponse from(Booking booking) {
      return new BookingResponse(
          booking.id().toString(),
          booking.carId().toString(),
          booking.borrowerId().toString(),
          booking.period().startDate(),
          booking.period().endDate(),
          booking.status().name());
    }
  }
}
