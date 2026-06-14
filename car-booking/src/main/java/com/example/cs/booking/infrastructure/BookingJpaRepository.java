package com.example.cs.booking.infrastructure;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface BookingJpaRepository extends JpaRepository<BookingJpaEntity, String> {

  @Query(
      "SELECT b FROM BookingJpaEntity b WHERE b.borrowerId = :borrowerId "
          + "AND b.status IN ('PENDING', 'ACTIVE')")
  Optional<BookingJpaEntity> findOngoingByBorrowerId(String borrowerId);

  @Query(
      "SELECT b FROM BookingJpaEntity b WHERE b.carId = :carId "
          + "AND b.status IN ('PENDING', 'ACTIVE')")
  Optional<BookingJpaEntity> findOngoingByCarId(String carId);

  @Query("SELECT b FROM BookingJpaEntity b WHERE b.status = 'ACTIVE' AND b.endDate < :today")
  List<BookingJpaEntity> findOverdueActive(LocalDate today);

  long countByStatus(String status);
}
