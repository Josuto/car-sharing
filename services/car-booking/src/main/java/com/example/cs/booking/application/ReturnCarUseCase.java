package com.example.cs.booking.application;

import com.example.cs.booking.domain.Booking;

public interface ReturnCarUseCase {
  Booking handle(ReturnCarCommand command);
}
