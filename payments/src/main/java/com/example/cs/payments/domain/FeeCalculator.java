package com.example.cs.payments.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class FeeCalculator {

  private static final BigDecimal DAILY_RATE = BigDecimal.valueOf(10);

  public Money calculate(LocalDate startDate, LocalDate endDate) {
    long days = Math.max(1, ChronoUnit.DAYS.between(startDate, endDate));
    return Money.ofEur(DAILY_RATE.multiply(BigDecimal.valueOf(days)));
  }
}
