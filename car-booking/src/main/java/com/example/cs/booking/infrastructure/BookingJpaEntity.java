package com.example.cs.booking.infrastructure;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bookings")
@Getter
@NoArgsConstructor
class BookingJpaEntity {

  @Id private String id;
  private String carId;
  private String status;
}
