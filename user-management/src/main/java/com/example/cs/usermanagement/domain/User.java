package com.example.cs.usermanagement.domain;

import com.example.cs.common.BorrowerFlaggedAsDebtor;
import com.example.cs.common.UserBankAccountChanged;
import com.example.cs.common.UserCreated;
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
  private String name;
  private String surname;
  private String bankAccount;
  private boolean isDebtor;
  private boolean isDeleted;

  @Getter(AccessLevel.NONE)
  private final List<Object> domainEvents = new ArrayList<>();

  private User(
      UUID id,
      String username,
      String name,
      String surname,
      String bankAccount,
      boolean isDebtor,
      boolean isDeleted) {
    this.id = id;
    this.username = username;
    this.name = name;
    this.surname = surname;
    this.bankAccount = bankAccount;
    this.isDebtor = isDebtor;
    this.isDeleted = isDeleted;
  }

  public static User create(
      UUID id, String username, String name, String surname, String bankAccount) {
    if (username == null || username.isBlank())
      throw new IllegalArgumentException("Username must not be blank");
    if (name == null || name.isBlank())
      throw new IllegalArgumentException("Name must not be blank");
    if (surname == null || surname.isBlank())
      throw new IllegalArgumentException("Surname must not be blank");
    if (bankAccount == null || bankAccount.isBlank())
      throw new IllegalArgumentException("Bank account must not be blank");
    var user = new User(id, username, name, surname, bankAccount, false, false);
    user.domainEvents.add(new UserCreated(id.toString(), username, name, surname, bankAccount));
    return user;
  }

  public static User reconstitute(
      UUID id,
      String username,
      String name,
      String surname,
      String bankAccount,
      boolean isDebtor,
      boolean isDeleted) {
    return new User(id, username, name, surname, bankAccount, isDebtor, isDeleted);
  }

  public void update(String name, String surname, String bankAccount) {
    if (name == null || name.isBlank())
      throw new IllegalArgumentException("Name must not be blank");
    if (surname == null || surname.isBlank())
      throw new IllegalArgumentException("Surname must not be blank");
    if (bankAccount == null || bankAccount.isBlank())
      throw new IllegalArgumentException("Bank account must not be blank");
    this.name = name;
    this.surname = surname;
    if (!bankAccount.equals(this.bankAccount)) {
      this.bankAccount = bankAccount;
      domainEvents.add(new UserBankAccountChanged(id.toString(), bankAccount));
    }
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
