package com.example.cs.booking.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BookingTest {

  private final UUID bookingId = UUID.randomUUID();
  private final UUID carId = UUID.randomUUID();
  private final UUID borrowerId = UUID.randomUUID();
  private final BookingPeriod period =
      BookingPeriod.of(LocalDate.now(), LocalDate.now().plusDays(3));

  @Test
  void create_storesFieldsAndSetsPendingStatus() {
    var booking = Booking.create(bookingId, carId, borrowerId, period);

    assertThat(booking.id()).isEqualTo(bookingId);
    assertThat(booking.carId()).isEqualTo(carId);
    assertThat(booking.borrowerId()).isEqualTo(borrowerId);
    assertThat(booking.period()).isEqualTo(period);
    assertThat(booking.status()).isEqualTo(BookingStatus.PENDING);
  }

  @Test
  void confirm_setsStatusToActive() {
    var booking = Booking.create(bookingId, carId, borrowerId, period);

    booking.confirm();

    assertThat(booking.status()).isEqualTo(BookingStatus.ACTIVE);
  }

  @Test
  void cancel_setsStatusToCancelled() {
    var booking = Booking.create(bookingId, carId, borrowerId, period);

    booking.cancel();

    assertThat(booking.status()).isEqualTo(BookingStatus.CANCELLED);
  }
}
