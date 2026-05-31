package com.example.cs.payments.infrastructure;

import com.example.cs.payments.application.CreateAccountHandler;
import com.example.cs.payments.application.CreateAccountUseCase;
import com.example.cs.payments.application.ProcessPaymentHandler;
import com.example.cs.payments.application.ProcessPaymentUseCase;
import com.example.cs.payments.application.UpdateBankAccountHandler;
import com.example.cs.payments.application.UpdateBankAccountUseCase;
import com.example.cs.payments.domain.AccountRepository;
import com.example.cs.payments.domain.BankingServicePort;
import com.example.cs.payments.domain.PaymentEventPublisher;
import com.example.cs.payments.domain.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
class PaymentsConfig {

  @Bean
  RestClient pspRestClient(@Value("${psp.base-url}") String pspBaseUrl) {
    return RestClient.builder().baseUrl(pspBaseUrl).build();
  }

  @Bean
  CreateAccountUseCase createAccountUseCase(AccountRepository repository) {
    return new CreateAccountHandler(repository);
  }

  @Bean
  UpdateBankAccountUseCase updateBankAccountUseCase(AccountRepository repository) {
    return new UpdateBankAccountHandler(repository);
  }

  @Bean
  ProcessPaymentUseCase processPaymentUseCase(
      AccountRepository accountRepository,
      TransactionRepository transactionRepository,
      BankingServicePort bankingService,
      PaymentEventPublisher publisher) {
    return new ProcessPaymentHandler(
        accountRepository, transactionRepository, bankingService, publisher);
  }
}
