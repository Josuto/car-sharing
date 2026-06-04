package com.example.cs.booking.application;

import java.time.LocalDate;
import java.util.UUID;

public record CreateBookingCommand(
    UUID carId, UUID borrowerId, LocalDate startDate, LocalDate endDate) {}
