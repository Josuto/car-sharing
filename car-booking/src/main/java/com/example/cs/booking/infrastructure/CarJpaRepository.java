package com.example.cs.booking.infrastructure;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface CarJpaRepository extends JpaRepository<CarJpaEntity, String> {

  @Query(
      "SELECT c FROM CarJpaEntity c WHERE NOT EXISTS "
          + "(SELECT b FROM BookingJpaEntity b WHERE b.carId = c.id "
          + "AND b.status IN ('PENDING', 'ACTIVE'))")
  List<CarJpaEntity> findAvailable();
}
