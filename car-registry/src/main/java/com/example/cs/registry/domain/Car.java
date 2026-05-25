package com.example.cs.registry.domain;

import com.example.cs.common.CarRegistered;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class Car {

  private static final String REGISTRATION_NUMBER_PATTERN = "^\\d{4}[A-Z]{3}$";

  private final UUID id;
  private final UUID ownerId;
  private final String type;
  private final String registrationNumber;

  @Getter(AccessLevel.NONE)
  private final List<Object> domainEvents = new ArrayList<>();

  private Car(UUID id, UUID ownerId, String type, String registrationNumber) {
    this.id = id;
    this.ownerId = ownerId;
    this.type = type;
    this.registrationNumber = registrationNumber;
  }

  public static Car create(UUID id, UUID ownerId, String type, String registrationNumber) {
    if (ownerId == null) throw new IllegalArgumentException("Owner ID must not be null");
    if (type == null || type.isBlank())
      throw new IllegalArgumentException("Type must not be blank");
    if (registrationNumber == null || !registrationNumber.matches(REGISTRATION_NUMBER_PATTERN))
      throw new IllegalArgumentException("Registration number must match pattern \\d{4}[A-Z]{3}");
    var car = new Car(id, ownerId, type, registrationNumber);
    car.domainEvents.add(
        new CarRegistered(id.toString(), ownerId.toString(), type, registrationNumber));
    return car;
  }

  public static Car reconstitute(UUID id, UUID ownerId, String type, String registrationNumber) {
    return new Car(id, ownerId, type, registrationNumber);
  }

  public List<Object> pullDomainEvents() {
    var events = List.copyOf(domainEvents);
    domainEvents.clear();
    return events;
  }
}
