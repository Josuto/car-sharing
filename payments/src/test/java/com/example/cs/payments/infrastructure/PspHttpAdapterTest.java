package com.example.cs.payments.infrastructure;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.cs.payments.domain.Money;
import com.example.cs.payments.domain.PaymentEventPublisher;
import com.example.cs.payments.domain.TransactionStatus;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

@SpringBootTest
@EnableWireMock(@ConfigureWireMock(name = "psp", baseUrlProperties = "psp.base-url"))
class PspHttpAdapterTest {

  @TestConfiguration
  static class TestConfig {
    @Bean
    PaymentEventPublisher noOpPaymentEventPublisher() {
      return event -> {};
    }
  }

  @InjectWireMock("psp")
  WireMockServer psp;

  @Autowired PspHttpAdapter adapter;

  @Test
  void process_pspReturns200_returnsSuccess() {
    psp.stubFor(post(urlEqualTo("/process")).willReturn(aResponse().withStatus(200)));

    var status = adapter.process("ES1234567890", Money.ofEur(BigDecimal.valueOf(30)));

    assertThat(status).isEqualTo(TransactionStatus.SUCCESS);
  }

  @Test
  void process_pspReturns409_returnsFailed() {
    psp.stubFor(post(urlEqualTo("/process")).willReturn(aResponse().withStatus(409)));

    var status = adapter.process("ES1234567890", Money.ofEur(BigDecimal.valueOf(30)));

    assertThat(status).isEqualTo(TransactionStatus.FAILED);
  }
}
