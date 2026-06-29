package com.example.cs.payments.infrastructure;

import com.example.cs.payments.domain.BankingServicePort;
import com.example.cs.payments.domain.Money;
import com.example.cs.payments.domain.TransactionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
class PspHttpAdapter implements BankingServicePort {

  private final RestClient pspRestClient;

  @Override
  public TransactionStatus process(String bankAccount, Money amount) {
    var status =
        pspRestClient
            .post()
            .uri("/process")
            .body(new PspRequest(bankAccount, amount.amount(), amount.currency()))
            .retrieve()
            .onStatus(s -> !s.is2xxSuccessful(), (request, response) -> {})
            .toBodilessEntity()
            .getStatusCode();
    if (status.is2xxSuccessful()) return TransactionStatus.SUCCESS;
    return status.value() == 409
        ? TransactionStatus.INSUFFICIENT_FUNDS
        : TransactionStatus.PSP_ERROR;
  }

  record PspRequest(String bankAccount, java.math.BigDecimal amount, String currency) {}
}
