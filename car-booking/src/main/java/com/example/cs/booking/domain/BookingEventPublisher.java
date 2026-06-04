package com.example.cs.booking.domain;

public interface BookingEventPublisher {
  void publish(Object event);
}
