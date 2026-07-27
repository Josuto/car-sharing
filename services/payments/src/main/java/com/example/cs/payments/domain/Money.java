package com.example.cs.payments.domain;

import java.math.BigDecimal;

public record Money(BigDecimal amount, String currency) {

  public static Money ofEur(BigDecimal amount) {
    return new Money(amount, "EUR");
  }
}
