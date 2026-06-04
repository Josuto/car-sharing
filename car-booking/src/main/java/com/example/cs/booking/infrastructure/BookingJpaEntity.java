package com.example.cs.booking.infrastructure;

import com.example.cs.booking.domain.Booking;
import com.example.cs.booking.domain.BookingPeriod;
import com.example.cs.booking.domain.BookingStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bookings")
@Getter
@NoArgsConstructor
@AllArgsConstructor
class BookingJpaEntity {

  @Id private String id;
  private String carId;
  private String borrowerId;
  private LocalDate startDate;
  private LocalDate endDate;
  private String status;

  static BookingJpaEntity from(Booking booking) {
    return new BookingJpaEntity(
        booking.id().toString(),
        booking.carId().toString(),
        booking.borrowerId().toString(),
        booking.period().startDate(),
        booking.period().endDate(),
        booking.status().name());
  }

  Booking toDomain() {
    return Booking.reconstitute(
        UUID.fromString(id),
        UUID.fromString(carId),
        UUID.fromString(borrowerId),
        BookingPeriod.reconstitute(startDate, endDate),
        BookingStatus.valueOf(status));
  }
}
