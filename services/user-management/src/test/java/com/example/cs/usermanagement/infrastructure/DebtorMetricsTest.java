package com.example.cs.usermanagement.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.cs.usermanagement.domain.UserRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

class DebtorMetricsTest {

  private final UserRepository userRepository = mock(UserRepository.class);
  private final SimpleMeterRegistry registry = new SimpleMeterRegistry();

  @Test
  void gauge_reflectsCurrentDebtorCount() {
    when(userRepository.countDebtors()).thenReturn(3L);
    new DebtorMetrics(userRepository).bindTo(registry);

    assertThat(registry.get("users_debtors_current").gauge().value()).isEqualTo(3.0);
  }
}
