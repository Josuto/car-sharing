package com.example.cs.booking.domain;

import java.util.Optional;
import java.util.UUID;

public interface BookingRepository {
  void save(Booking booking);

  Optional<Booking> findById(UUID id);

  Optional<Booking> findOngoingByBorrowerId(UUID borrowerId);

  Optional<Booking> findOngoingByCarId(UUID carId);
}
