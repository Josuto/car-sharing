package com.example.cs.common;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class EventDtosTest {

	@Test
	void dtosCanBeInstantiated() {
		assertNotNull(new CarRegistered("id", "ownerId", "SEDAN", "REG-001"));
		assertNotNull(new UserCreated("id", "jdoe", "John", "Doe"));
		assertNotNull(new BookingPaymentRequested("bookingId", "borrowerId", "carId",
				LocalDate.now(), LocalDate.now().plusDays(3), BigDecimal.valueOf(30)));
		assertNotNull(new PaymentProcessed("bookingId", true));
		assertNotNull(new BorrowerFlaggedAsDebtor("userId"));
	}
}
