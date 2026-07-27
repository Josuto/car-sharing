package com.example.cs.booking.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cs.booking.domain.Booking;
import com.example.cs.booking.domain.BookingNotFoundException;
import com.example.cs.booking.domain.BookingPeriod;
import com.example.cs.booking.domain.BookingRepository;
import com.example.cs.booking.domain.BookingStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ReturnCarHandlerTest {

  private final BookingRepository bookingRepository = mock(BookingRepository.class);
  private final ReturnCarHandler handler = new ReturnCarHandler(bookingRepository);

  private final UUID bookingId = UUID.randomUUID();
  private final UUID carId = UUID.randomUUID();
  private final UUID borrowerId = UUID.randomUUID();
  private final BookingPeriod period =
      BookingPeriod.reconstitute(LocalDate.now().minusDays(2), LocalDate.now().plusDays(1));

  @Test
  void handle_withActiveBooking_savesReturnedBookingAndReturnsIt() {
    var booking =
        Booking.reconstitute(
            bookingId, carId, borrowerId, period, Instant.now(), BookingStatus.ACTIVE);
    when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

    var result = handler.handle(new ReturnCarCommand(bookingId));

    assertThat(result.status()).isEqualTo(BookingStatus.RETURNED);
    verify(bookingRepository).save(argThat(saved -> saved.status() == BookingStatus.RETURNED));
  }

  @Test
  void handle_withUnknownBooking_throws() {
    when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> handler.handle(new ReturnCarCommand(bookingId)))
        .isInstanceOf(BookingNotFoundException.class);
  }

  @Test
  void handle_withPendingBooking_throws() {
    var booking =
        Booking.reconstitute(
            bookingId, carId, borrowerId, period, Instant.now(), BookingStatus.PENDING);
    when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

    assertThatThrownBy(() -> handler.handle(new ReturnCarCommand(bookingId)))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void handle_withCancelledBooking_throws() {
    var booking =
        Booking.reconstitute(
            bookingId, carId, borrowerId, period, Instant.now(), BookingStatus.CANCELLED);
    when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

    assertThatThrownBy(() -> handler.handle(new ReturnCarCommand(bookingId)))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void handle_withAlreadyReturnedBooking_throws() {
    var booking =
        Booking.reconstitute(
            bookingId, carId, borrowerId, period, Instant.now(), BookingStatus.RETURNED);
    when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

    assertThatThrownBy(() -> handler.handle(new ReturnCarCommand(bookingId)))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
