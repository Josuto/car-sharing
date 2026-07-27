package com.example.cs.registry.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.example.cs.common.CarRegistered;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CarTest {

  @Test
  void create_withValidData_returnsCarWithCorrectFieldsAndRaisesCarRegisteredEvent() {
    var id = UUID.randomUUID();
    var ownerId = UUID.randomUUID();

    var car = Car.create(id, ownerId, "SEDAN", "1234ABC");

    assertThat(car.id()).isEqualTo(id);
    assertThat(car.ownerId()).isEqualTo(ownerId);
    assertThat(car.type()).isEqualTo("SEDAN");
    assertThat(car.registrationNumber()).isEqualTo("1234ABC");
    assertThat(car.pullDomainEvents()).singleElement().isInstanceOf(CarRegistered.class);
  }

  @Test
  void create_withNullOwnerId_throws() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> Car.create(UUID.randomUUID(), null, "SEDAN", "1234ABC"));
  }

  @Test
  void create_withBlankType_throws() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> Car.create(UUID.randomUUID(), UUID.randomUUID(), " ", "1234ABC"));
  }

  @Test
  void create_withInvalidRegistrationNumber_throws() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> Car.create(UUID.randomUUID(), UUID.randomUUID(), "SEDAN", "ABC1234"));
  }
}
