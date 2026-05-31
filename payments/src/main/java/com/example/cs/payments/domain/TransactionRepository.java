package com.example.cs.payments.domain;

public interface TransactionRepository {
  void save(Transaction transaction);
}
