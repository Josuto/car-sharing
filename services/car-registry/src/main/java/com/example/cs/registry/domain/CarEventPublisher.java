package com.example.cs.registry.domain;

public interface CarEventPublisher {
  void publish(Object event);
}
