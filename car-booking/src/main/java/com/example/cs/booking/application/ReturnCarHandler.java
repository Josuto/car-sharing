package com.example.cs.booking.application;

import com.example.cs.booking.domain.Booking;
import com.example.cs.booking.domain.BookingNotFoundException;
import com.example.cs.booking.domain.BookingRepository;
import com.example.cs.booking.domain.BookingStatus;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReturnCarHandler implements ReturnCarUseCase {

  private final BookingRepository bookingRepository;

  @Override
  public Booking handle(ReturnCarCommand command) {
    var booking =
        bookingRepository
            .findById(command.bookingId())
            .orElseThrow(() -> new BookingNotFoundException(command.bookingId()));
    if (booking.status() != BookingStatus.ACTIVE) {
      throw new IllegalArgumentException(
          "Cannot return a booking with status: " + booking.status());
    }
    booking.returnCar();
    bookingRepository.save(booking);
    return booking;
  }
}
