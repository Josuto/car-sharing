package com.example.cs.registry.infrastructure;

import com.example.cs.registry.domain.Car;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cars")
@Getter
@NoArgsConstructor
@AllArgsConstructor
class CarJpaEntity {

  @Id private String id;

  @Column(nullable = false)
  private String ownerId;

  @Column(nullable = false)
  private String type;

  @Column(unique = true, nullable = false)
  private String registrationNumber;

  static CarJpaEntity from(Car car) {
    return new CarJpaEntity(
        car.id().toString(), car.ownerId().toString(), car.type(), car.registrationNumber());
  }

  Car toDomain() {
    return Car.reconstitute(
        UUID.fromString(id), UUID.fromString(ownerId), type, registrationNumber);
  }
}
