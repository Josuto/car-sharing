package com.example.cs.payments.domain;

public interface BankingServicePort {
  TransactionStatus process(String bankAccount, Money amount);
}
