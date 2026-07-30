package com.example.cs.common;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class EventDtosTest {

  @Test
  void dtosCanBeInstantiated() {
    assertNotNull(new CarRegistered("id", "ownerId", "SEDAN", "REG-001"));
    assertNotNull(new UserCreated("id", "jdoe", "John", "Doe", "ES1234567890"));
    assertNotNull(new UserBankAccountChanged("id", "ES1234567890"));
    assertNotNull(
        new BookingPaymentRequested(
            "bookingId", "borrowerId", "carId", LocalDate.now(), LocalDate.now().plusDays(3)));
    assertNotNull(new PaymentProcessed("bookingId", true));
    assertNotNull(new BorrowerFlaggedAsDebtor("userId"));
  }
}
