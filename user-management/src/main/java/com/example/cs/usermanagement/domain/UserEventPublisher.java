package com.example.cs.usermanagement.domain;

public interface UserEventPublisher {
    void publish(Object event);
}
