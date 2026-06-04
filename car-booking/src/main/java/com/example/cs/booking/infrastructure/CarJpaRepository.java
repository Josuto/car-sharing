package com.example.cs.booking.infrastructure;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

interface CarJpaRepository extends JpaRepository<CarJpaEntity, String> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<CarJpaEntity> findById(String id);

  @Query(
      "SELECT c FROM CarJpaEntity c WHERE NOT EXISTS "
          + "(SELECT b FROM BookingJpaEntity b WHERE b.carId = c.id "
          + "AND b.status IN ('PENDING', 'ACTIVE'))")
  List<CarJpaEntity> findAvailable();
}
