package com.example.cs.booking.infrastructure;

import com.example.cs.booking.application.FlagLateReturnDebtorsHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LateReturnDebtorScheduler {

  private final FlagLateReturnDebtorsHandler flagLateReturnDebtorsHandler;

  @Scheduled(
      fixedRate = 15 * 60 * 1000) // Every 15 min; in production, use a less frequent schedule
  void run() {
    flagLateReturnDebtorsHandler.handle();
  }
}
