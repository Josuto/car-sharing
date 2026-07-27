package com.example.cs.payments.application;

public interface ProcessPaymentUseCase {
  void handle(ProcessPaymentCommand command);
}
