package com.example.cs.booking.infrastructure;

import com.example.cs.booking.domain.Booking;
import com.example.cs.booking.domain.BookingRepository;
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
}
