package com.example.cs.payments.application;

import java.time.LocalDate;

public record ProcessPaymentCommand(
    String bookingId, String borrowerId, LocalDate startDate, LocalDate endDate) {}
