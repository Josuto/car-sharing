package com.example.cs.booking.infrastructure;

import com.example.cs.booking.domain.Car;
import com.example.cs.booking.domain.CarRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class CarJpaAdapter implements CarRepository {

  private final CarJpaRepository jpaRepository;

  @Override
  public List<Car> findAvailable() {
    return jpaRepository.findAvailable().stream().map(CarJpaEntity::toDomain).toList();
  }

  @Override
  public void save(Car car) {
    jpaRepository.save(CarJpaEntity.from(car));
  }
}
