package com.example.cs.payments.application;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cs.common.PaymentProcessed;
import com.example.cs.payments.domain.Account;
import com.example.cs.payments.domain.AccountRepository;
import com.example.cs.payments.domain.BankingServicePort;
import com.example.cs.payments.domain.PaymentEventPublisher;
import com.example.cs.payments.domain.TransactionRepository;
import com.example.cs.payments.domain.TransactionStatus;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProcessPaymentHandlerTest {

  private final AccountRepository accountRepository = mock(AccountRepository.class);
  private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
  private final BankingServicePort bankingService = mock(BankingServicePort.class);
  private final PaymentEventPublisher publisher = mock(PaymentEventPublisher.class);
  private final ProcessPaymentHandler handler =
      new ProcessPaymentHandler(
          accountRepository, transactionRepository, bankingService, publisher);

  @Test
  void handle_pspReturnsSuccess_persistsSuccessTransactionAndEmitsPaymentProcessed() {
    var bookingId = UUID.randomUUID();
    var borrowerId = UUID.randomUUID();
    var account = Account.create(borrowerId, "ES1234567890");
    when(accountRepository.findByUserId(borrowerId)).thenReturn(Optional.of(account));
    when(bankingService.process(eq("ES1234567890"), argThat(money -> true)))
        .thenReturn(TransactionStatus.SUCCESS);

    handler.handle(
        new ProcessPaymentCommand(
            bookingId.toString(),
            borrowerId.toString(),
            LocalDate.of(2026, 6, 1),
            LocalDate.of(2026, 6, 4)));

    verify(transactionRepository)
        .save(
            argThat(
                transaction ->
                    transaction.bookingId().equals(bookingId)
                        && transaction.status() == TransactionStatus.SUCCESS));
    verify(publisher)
        .publish(
            argThat(
                event ->
                    event instanceof PaymentProcessed paymentProcessed
                        && paymentProcessed.bookingId().equals(bookingId.toString())
                        && paymentProcessed.success()));
  }

  @Test
  void handle_pspReturnsFailed_persistsFailedTransactionAndEmitsPaymentProcessed() {
    var bookingId = UUID.randomUUID();
    var borrowerId = UUID.randomUUID();
    var account = Account.create(borrowerId, "ES1234567890");
    when(accountRepository.findByUserId(borrowerId)).thenReturn(Optional.of(account));
    when(bankingService.process(eq("ES1234567890"), argThat(money -> true)))
        .thenReturn(TransactionStatus.FAILED);

    handler.handle(
        new ProcessPaymentCommand(
            bookingId.toString(),
            borrowerId.toString(),
            LocalDate.of(2026, 6, 1),
            LocalDate.of(2026, 6, 4)));

    verify(transactionRepository)
        .save(
            argThat(
                transaction ->
                    transaction.bookingId().equals(bookingId)
                        && transaction.status() == TransactionStatus.FAILED));
    verify(publisher)
        .publish(
            argThat(
                event ->
                    event instanceof PaymentProcessed paymentProcessed
                        && paymentProcessed.bookingId().equals(bookingId.toString())
                        && !paymentProcessed.success()));
  }
}
