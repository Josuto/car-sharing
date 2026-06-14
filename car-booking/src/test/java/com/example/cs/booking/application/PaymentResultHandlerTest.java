package com.example.cs.booking.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cs.booking.domain.Booking;
import com.example.cs.booking.domain.BookingPeriod;
import com.example.cs.booking.domain.BookingRepository;
import com.example.cs.booking.domain.BookingStatus;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PaymentResultHandlerTest {

  private final BookingRepository bookingRepository = mock(BookingRepository.class);
  private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
  private final PaymentResultHandler handler =
      new PaymentResultHandler(bookingRepository, meterRegistry);

  private final UUID bookingId = UUID.randomUUID();
  private final Booking pendingBooking =
      Booking.reconstitute(
          bookingId,
          UUID.randomUUID(),
          UUID.randomUUID(),
          BookingPeriod.of(LocalDate.now(), LocalDate.now().plusDays(2)),
          Instant.now().minusSeconds(10),
          BookingStatus.PENDING);

  @Test
  void handle_withSuccess_setsBookingActiveAndRecordsSagaDuration() {
    when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(pendingBooking));

    handler.handle(new PaymentResultCommand(bookingId.toString(), true));

    verify(bookingRepository).save(argThat(booking -> booking.status() == BookingStatus.ACTIVE));
    assertThat(meterRegistry.timer("bookings.saga.duration").count()).isEqualTo(1);
  }

  @Test
  void handle_withFailure_setsBookingCancelledAndRecordsSagaDuration() {
    when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(pendingBooking));

    handler.handle(new PaymentResultCommand(bookingId.toString(), false));

    verify(bookingRepository).save(argThat(booking -> booking.status() == BookingStatus.CANCELLED));
    assertThat(meterRegistry.timer("bookings.saga.duration").count()).isEqualTo(1);
  }

  @Test
  void handle_withUnknownBookingId_doesNotSave() {
    when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

    handler.handle(new PaymentResultCommand(bookingId.toString(), true));

    verify(bookingRepository, never()).save(argThat(booking -> true));
  }
}
