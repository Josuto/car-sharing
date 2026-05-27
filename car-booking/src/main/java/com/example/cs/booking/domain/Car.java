package com.example.cs.booking.domain;

import java.util.UUID;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class Car {

  private final UUID id;
  private final String type;

  private Car(UUID id, String type) {
    this.id = id;
    this.type = type;
  }

  public static Car reconstitute(UUID id, String type) {
    return new Car(id, type);
  }
}
