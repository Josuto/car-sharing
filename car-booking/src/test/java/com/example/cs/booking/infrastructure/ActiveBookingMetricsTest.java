package com.example.cs.booking.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.cs.booking.domain.BookingRepository;
import com.example.cs.booking.domain.BookingStatus;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

class ActiveBookingMetricsTest {

  private final BookingRepository bookingRepository = mock(BookingRepository.class);
  private final SimpleMeterRegistry registry = new SimpleMeterRegistry();

  @Test
  void gauge_reflectsCurrentActiveBookingCount() {
    when(bookingRepository.countByStatus(BookingStatus.ACTIVE)).thenReturn(7L);
    new ActiveBookingMetrics(bookingRepository).bindTo(registry);

    assertThat(registry.get("bookings_active_current").gauge().value()).isEqualTo(7.0);
  }
}
