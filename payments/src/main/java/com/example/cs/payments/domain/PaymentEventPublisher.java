package com.example.cs.payments.domain;

public interface PaymentEventPublisher {
  void publish(Object event);
}
