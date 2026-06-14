package com.example.cs.payments.infrastructure;

import com.example.cs.payments.domain.BankingServicePort;
import com.example.cs.payments.domain.Money;
import com.example.cs.payments.domain.TransactionStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
class PspHttpAdapter implements BankingServicePort {

  private static final Logger log = LoggerFactory.getLogger(PspHttpAdapter.class);

  private final RestClient pspRestClient;

  @Override
  public TransactionStatus process(String bankAccount, Money amount) {
    var status =
        pspRestClient
            .post()
            .uri("/process")
            .body(new PspRequest(bankAccount, amount.amount(), amount.currency()))
            .retrieve()
            .onStatus(
                s -> !s.is2xxSuccessful(),
                (request, response) -> {
                  if (response.getStatusCode().value() != 409) {
                    log.warn("Transient PSP failure, status={}", response.getStatusCode().value());
                  }
                })
            .toBodilessEntity()
            .getStatusCode();
    return status.is2xxSuccessful() ? TransactionStatus.SUCCESS : TransactionStatus.FAILED;
  }

  record PspRequest(String bankAccount, java.math.BigDecimal amount, String currency) {}
}
