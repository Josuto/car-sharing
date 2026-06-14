package com.example.cs.booking.domain;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class Booking {

  private final UUID id;
  private final UUID carId;
  private final UUID borrowerId;
  private final BookingPeriod period;
  private final Instant createdAt;
  private BookingStatus status;

  private Booking(
      UUID id,
      UUID carId,
      UUID borrowerId,
      BookingPeriod period,
      Instant createdAt,
      BookingStatus status) {
    this.id = id;
    this.carId = carId;
    this.borrowerId = borrowerId;
    this.period = period;
    this.createdAt = createdAt;
    this.status = status;
  }

  public static Booking create(UUID id, UUID carId, UUID borrowerId, BookingPeriod period) {
    return new Booking(id, carId, borrowerId, period, Instant.now(), BookingStatus.PENDING);
  }

  public static Booking reconstitute(
      UUID id,
      UUID carId,
      UUID borrowerId,
      BookingPeriod period,
      Instant createdAt,
      BookingStatus status) {
    return new Booking(id, carId, borrowerId, period, createdAt, status);
  }

  public void confirm() {
    this.status = BookingStatus.ACTIVE;
  }

  public void cancel() {
    this.status = BookingStatus.CANCELLED;
  }

  public void returnCar() {
    this.status = BookingStatus.RETURNED;
  }
}
