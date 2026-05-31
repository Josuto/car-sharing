package com.example.cs.payments;

import com.example.cs.payments.domain.PaymentEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
class PaymentsApplicationTests {

  @TestConfiguration
  static class TestConfig {
    @Bean
    PaymentEventPublisher noOpPaymentEventPublisher() {
      return event -> {};
    }
  }

  @Test
  void contextLoads() {}
}
