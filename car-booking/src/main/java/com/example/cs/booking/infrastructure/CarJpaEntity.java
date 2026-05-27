package com.example.cs.booking.infrastructure;

import com.example.cs.booking.domain.Car;
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
  private String type;

  static CarJpaEntity from(Car car) {
    return new CarJpaEntity(car.id().toString(), car.type());
  }

  Car toDomain() {
    return Car.reconstitute(UUID.fromString(id), type);
  }
}
