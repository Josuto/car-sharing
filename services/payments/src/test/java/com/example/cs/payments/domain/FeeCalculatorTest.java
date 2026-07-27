package com.example.cs.payments.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class FeeCalculatorTest {

  private final FeeCalculator calculator = new FeeCalculator();

  @Test
  void calculate_multiDayBooking_returnsFeeof10EurPerDay() {
    var start = LocalDate.of(2026, 6, 1);
    var end = LocalDate.of(2026, 6, 4);

    var fee = calculator.calculate(start, end);

    assertThat(fee.amount()).isEqualByComparingTo(BigDecimal.valueOf(30));
    assertThat(fee.currency()).isEqualTo("EUR");
  }

  @Test
  void calculate_oneDayBooking_returns10Eur() {
    var start = LocalDate.of(2026, 6, 1);
    var end = LocalDate.of(2026, 6, 2);

    var fee = calculator.calculate(start, end);

    assertThat(fee.amount()).isEqualByComparingTo(BigDecimal.valueOf(10));
  }

  @Test
  void calculate_sameDayBooking_returns10Eur() {
    var date = LocalDate.of(2026, 6, 1);

    var fee = calculator.calculate(date, date);

    assertThat(fee.amount()).isEqualByComparingTo(BigDecimal.valueOf(10));
  }
}
