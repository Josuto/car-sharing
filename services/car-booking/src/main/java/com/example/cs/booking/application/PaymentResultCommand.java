package com.example.cs.booking.application;

public record PaymentResultCommand(String bookingId, boolean success) {}
