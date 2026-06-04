package com.example.cs.booking.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class BookingPeriod {

  private final LocalDate startDate;
  private final LocalDate endDate;

  private BookingPeriod(LocalDate startDate, LocalDate endDate) {
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public static BookingPeriod of(LocalDate startDate, LocalDate endDate) {
    if (startDate.isBefore(LocalDate.now())) {
      throw new IllegalArgumentException("startDate must not be in the past");
    }
    if (endDate.isBefore(startDate)) {
      throw new IllegalArgumentException("endDate must not be before startDate");
    }
    if (ChronoUnit.DAYS.between(startDate, endDate) > 14) {
      throw new IllegalArgumentException("booking period must not exceed 15 days");
    }
    return new BookingPeriod(startDate, endDate);
  }

  public static BookingPeriod reconstitute(LocalDate startDate, LocalDate endDate) {
    return new BookingPeriod(startDate, endDate);
  }
}
