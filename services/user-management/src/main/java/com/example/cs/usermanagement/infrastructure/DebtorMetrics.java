package com.example.cs.usermanagement.infrastructure;

import com.example.cs.usermanagement.domain.UserRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class DebtorMetrics implements MeterBinder {

  private final UserRepository userRepository;

  @Override
  public void bindTo(MeterRegistry registry) {
    Gauge.builder("users.debtors.current", userRepository, r -> r.countDebtors())
        .register(registry);
  }
}
