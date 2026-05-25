package com.example.cs.registry.infrastructure;

import com.example.cs.registry.domain.Car;
import com.example.cs.registry.domain.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class CarJpaAdapter implements CarRepository {

  private final CarJpaRepository jpaRepository;

  @Override
  public void save(Car car) {
    jpaRepository.save(CarJpaEntity.from(car));
  }

  @Override
  public boolean existsByRegistrationNumber(String registrationNumber) {
    return jpaRepository.existsByRegistrationNumber(registrationNumber);
  }
}
