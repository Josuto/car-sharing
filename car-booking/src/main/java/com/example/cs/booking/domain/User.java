package com.example.cs.booking.domain;

import java.util.UUID;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class User {

  private final UUID id;
  private final boolean isDebtor;

  private User(UUID id, boolean isDebtor) {
    this.id = id;
    this.isDebtor = isDebtor;
  }

  public static User reconstitute(UUID id, boolean isDebtor) {
    return new User(id, isDebtor);
  }
}
