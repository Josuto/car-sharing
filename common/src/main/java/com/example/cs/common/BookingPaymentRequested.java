package com.example.cs.common;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingPaymentRequested(
        String bookingId,
        String borrowerId,
        String carId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal amount) {}
