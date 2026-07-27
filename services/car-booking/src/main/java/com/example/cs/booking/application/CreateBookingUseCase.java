package com.example.cs.booking.application;

import com.example.cs.booking.domain.Booking;

public interface CreateBookingUseCase {
  Booking handle(CreateBookingCommand command);
}
