package com.example.cs.booking.infrastructure;

import com.example.cs.booking.application.FlagLateReturnDebtorsHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LateReturnDebtorScheduler {

  private final FlagLateReturnDebtorsHandler flagLateReturnDebtorsHandler;

  @Scheduled(cron = "0 0 0 * * *") // Every day at midnight
  void run() {
    flagLateReturnDebtorsHandler.handle();
  }
}
