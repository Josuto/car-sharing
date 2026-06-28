package com.example.cs.booking.application;

import com.example.cs.booking.domain.BookingRepository;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class PaymentResultHandler implements PaymentResultUseCase {

  private static final Logger log = LoggerFactory.getLogger(PaymentResultHandler.class);

  private final BookingRepository bookingRepository;
  private final MeterRegistry meterRegistry;

  @Override
  public void handle(PaymentResultCommand command) {
    var bookingId = UUID.fromString(command.bookingId());
    var booking = bookingRepository.findById(bookingId);

    if (booking.isEmpty()) {
      log.warn("Received PaymentProcessed for unknown bookingId={}", command.bookingId());
      return;
    }

    if (command.success()) {
      booking.get().confirm();
    } else {
      booking.get().cancel();
    }
    bookingRepository.save(booking.get());

    var duration = Duration.between(booking.get().createdAt(), Instant.now());
    meterRegistry
        .timer("bookings.saga.duration")
        .record(duration.isNegative() ? Duration.ZERO : duration);
  }
}
