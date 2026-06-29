package com.example.cs.payments.application;

import com.example.cs.common.PaymentProcessed;
import com.example.cs.payments.domain.AccountRepository;
import com.example.cs.payments.domain.BankingServicePort;
import com.example.cs.payments.domain.FeeCalculator;
import com.example.cs.payments.domain.PaymentEventPublisher;
import com.example.cs.payments.domain.Transaction;
import com.example.cs.payments.domain.TransactionRepository;
import com.example.cs.payments.domain.TransactionStatus;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class ProcessPaymentHandler implements ProcessPaymentUseCase {

  private static final Logger log = LoggerFactory.getLogger(ProcessPaymentHandler.class);

  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;
  private final BankingServicePort bankingService;
  private final PaymentEventPublisher publisher;
  private final MeterRegistry meterRegistry;
  private final FeeCalculator feeCalculator = new FeeCalculator();

  @Override
  public void handle(ProcessPaymentCommand command) {
    var borrowerId = UUID.fromString(command.borrowerId());
    var bookingId = UUID.fromString(command.bookingId());
    var account =
        accountRepository
            .findByUserId(borrowerId)
            .orElseThrow(
                () -> new IllegalStateException("No account found for user: " + borrowerId));
    var fee = feeCalculator.calculate(command.startDate(), command.endDate());
    var status = bankingService.process(account.bankAccount(), fee);
    transactionRepository.save(Transaction.create(bookingId, borrowerId, fee, status));
    publisher.publish(
        new PaymentProcessed(command.bookingId(), status == TransactionStatus.SUCCESS));
    if (status == TransactionStatus.INSUFFICIENT_FUNDS) {
      meterRegistry.counter("bookings.outcome", "result", "insufficient_funds").increment();
      log.warn(
          "payment.failed.insufficient_funds bookingId={} borrowerId={} fee={}",
          bookingId,
          borrowerId,
          fee);
    } else if (status == TransactionStatus.PSP_ERROR) {
      meterRegistry.counter("bookings.outcome", "result", "psp_error").increment();
      log.warn(
          "payment.failed.psp_error bookingId={} borrowerId={} fee={}", bookingId, borrowerId, fee);
    } else {
      meterRegistry.counter("bookings.outcome", "result", "success").increment();
    }
  }
}
