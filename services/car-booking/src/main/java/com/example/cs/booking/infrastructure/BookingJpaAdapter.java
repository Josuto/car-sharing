package com.example.cs.booking.infrastructure;

import com.example.cs.booking.domain.Booking;
import com.example.cs.booking.domain.BookingRepository;
import com.example.cs.booking.domain.BookingStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class BookingJpaAdapter implements BookingRepository {

  private final BookingJpaRepository jpaRepository;

  @Override
  public void save(Booking booking) {
    jpaRepository.save(BookingJpaEntity.from(booking));
  }

  @Override
  public Optional<Booking> findById(UUID id) {
    return jpaRepository.findById(id.toString()).map(BookingJpaEntity::toDomain);
  }

  @Override
  public Optional<Booking> findOngoingByBorrowerId(UUID borrowerId) {
    return jpaRepository
        .findOngoingByBorrowerId(borrowerId.toString())
        .map(BookingJpaEntity::toDomain);
  }

  @Override
  public Optional<Booking> findOngoingByCarId(UUID carId) {
    return jpaRepository.findOngoingByCarId(carId.toString()).map(BookingJpaEntity::toDomain);
  }

  @Override
  public List<Booking> findOverdueActive() {
    return jpaRepository.findOverdueActive(LocalDate.now()).stream()
        .map(BookingJpaEntity::toDomain)
        .toList();
  }

  @Override
  public long countByStatus(BookingStatus status) {
    return jpaRepository.countByStatus(status.name());
  }
}
