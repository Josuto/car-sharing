package com.example.cs.booking.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class BookingPeriodTest {

  @Test
  void of_withValidMultiDayPeriod_storesDates() {
    var start = LocalDate.now();
    var end = start.plusDays(3);

    var period = BookingPeriod.of(start, end);

    assertThat(period.startDate()).isEqualTo(start);
    assertThat(period.endDate()).isEqualTo(end);
  }

  @Test
  void of_withSameDayPeriod_isValid() {
    var today = LocalDate.now();
    assertThatCode(() -> BookingPeriod.of(today, today)).doesNotThrowAnyException();
  }

  @Test
  void of_withEndDateBeforeStartDate_throws() {
    var start = LocalDate.now();
    assertThatThrownBy(() -> BookingPeriod.of(start, start.minusDays(1)))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void of_withPeriodExceedingFifteenDays_throws() {
    var start = LocalDate.now();
    assertThatThrownBy(() -> BookingPeriod.of(start, start.plusDays(15)))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void of_withStartDateInPast_throws() {
    var yesterday = LocalDate.now().minusDays(1);
    assertThatThrownBy(() -> BookingPeriod.of(yesterday, yesterday.plusDays(3)))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
