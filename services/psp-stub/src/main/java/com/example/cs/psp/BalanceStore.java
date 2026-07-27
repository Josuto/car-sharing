package com.example.cs.psp;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
class BalanceStore {

  private final Map<String, BigDecimal> balances =
      new HashMap<>(
          Map.of(
              "ES341234567890", new BigDecimal("30.00"),
              "ES341234567891", new BigDecimal("0.00"),
              "ES341234567892", new BigDecimal("200.00")));

  synchronized boolean deduct(String account, BigDecimal amount) {
    BigDecimal current = balances.get(account);
    if (current == null || current.compareTo(amount) < 0) return false;
    balances.put(account, current.subtract(amount));
    return true;
  }

  synchronized boolean exists(String account) {
    return balances.containsKey(account);
  }

  synchronized Map<String, BigDecimal> all() {
    return Map.copyOf(balances);
  }
}
