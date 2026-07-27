package com.example.cs.booking.domain;

import java.util.UUID;

public class BookingNotFoundException extends RuntimeException {

  public BookingNotFoundException(UUID id) {
    super("Booking not found: " + id);
  }
}
