package com.example.cs.payments.infrastructure;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.example.cs.payments.domain.Money;
import com.example.cs.payments.domain.PaymentEventPublisher;
import com.example.cs.payments.domain.TransactionStatus;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
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
  void process_pspReturns409_returnsFailedWithoutWarn() {
    psp.stubFor(post(urlEqualTo("/process")).willReturn(aResponse().withStatus(409)));
    var logs = captureAdapterLogs();

    var status = adapter.process("ES1234567890", Money.ofEur(BigDecimal.valueOf(30)));

    assertThat(status).isEqualTo(TransactionStatus.FAILED);
    assertThat(logs.list).noneMatch(e -> e.getLevel() == Level.WARN);
  }

  @Test
  void process_pspReturnsTransientFailure_logsWarnAndReturnsFailed() {
    psp.stubFor(post(urlEqualTo("/process")).willReturn(aResponse().withStatus(500)));
    var logs = captureAdapterLogs();

    var status = adapter.process("ES1234567890", Money.ofEur(BigDecimal.valueOf(30)));

    assertThat(status).isEqualTo(TransactionStatus.FAILED);
    assertThat(logs.list)
        .anyMatch(e -> e.getLevel() == Level.WARN && e.getFormattedMessage().contains("500"));
  }

  private ListAppender<ILoggingEvent> captureAdapterLogs() {
    var logger = (Logger) LoggerFactory.getLogger(PspHttpAdapter.class);
    var appender = new ListAppender<ILoggingEvent>();
    appender.start();
    logger.addAppender(appender);
    return appender;
  }
}
