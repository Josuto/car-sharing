package com.example.cs.booking.infrastructure;

import com.example.cs.booking.domain.BookingRepository;
import com.example.cs.booking.domain.BookingStatus;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class ActiveBookingMetrics implements MeterBinder {

  private final BookingRepository bookingRepository;

  @Override
  public void bindTo(MeterRegistry registry) {
    Gauge.builder(
            "bookings.active.current",
            bookingRepository,
            r -> r.countByStatus(BookingStatus.ACTIVE))
        .register(registry);
  }
}
