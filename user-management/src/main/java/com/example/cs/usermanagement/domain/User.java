package com.example.cs.usermanagement.domain;

import com.example.cs.common.BorrowerFlaggedAsDebtor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class User {

  private final UUID id;
  private final String username;
  private final String name;
  private final String surname;
  private boolean isDebtor;
  private boolean isDeleted;

  @Getter(AccessLevel.NONE)
  private final List<Object> domainEvents = new ArrayList<>();

  private User(
      UUID id, String username, String name, String surname, boolean isDebtor, boolean isDeleted) {
    this.id = id;
    this.username = username;
    this.name = name;
    this.surname = surname;
    this.isDebtor = isDebtor;
    this.isDeleted = isDeleted;
  }

  public static User create(UUID id, String username, String name, String surname) {
    if (username == null || username.isBlank())
      throw new IllegalArgumentException("Username must not be blank");
    if (name == null || name.isBlank())
      throw new IllegalArgumentException("Name must not be blank");
    if (surname == null || surname.isBlank())
      throw new IllegalArgumentException("Surname must not be blank");
    return new User(id, username, name, surname, false, false);
  }

  public static User reconstitute(
      UUID id, String username, String name, String surname, boolean isDebtor, boolean isDeleted) {
    return new User(id, username, name, surname, isDebtor, isDeleted);
  }

  public void flagAsDebtor() {
    this.isDebtor = true;
    domainEvents.add(new BorrowerFlaggedAsDebtor(id.toString()));
  }

  public void delete() {
    this.isDeleted = true;
  }

  public List<Object> pullDomainEvents() {
    var events = List.copyOf(domainEvents);
    domainEvents.clear();
    return events;
  }
}
