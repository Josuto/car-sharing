package com.example.cs.booking.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cs.booking.domain.Booking;
import com.example.cs.booking.domain.BookingEventPublisher;
import com.example.cs.booking.domain.BookingPeriod;
import com.example.cs.booking.domain.BookingRepository;
import com.example.cs.booking.domain.BookingStatus;
import com.example.cs.booking.domain.Car;
import com.example.cs.booking.domain.CarRepository;
import com.example.cs.booking.domain.User;
import com.example.cs.booking.domain.UserRepository;
import com.example.cs.common.BookingPaymentRequested;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CreateBookingHandlerTest {

  private final CarRepository carRepository = mock(CarRepository.class);
  private final UserRepository userRepository = mock(UserRepository.class);
  private final BookingRepository bookingRepository = mock(BookingRepository.class);
  private final BookingEventPublisher publisher = mock(BookingEventPublisher.class);
  private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
  private final CreateBookingHandler handler =
      new CreateBookingHandler(
          carRepository, userRepository, bookingRepository, publisher, meterRegistry);

  private final UUID carId = UUID.randomUUID();
  private final UUID borrowerId = UUID.randomUUID();
  private final LocalDate start = LocalDate.now();
  private final LocalDate end = start.plusDays(3);
  private final CreateBookingCommand command =
      new CreateBookingCommand(carId, borrowerId, start, end);

  @Test
  void handle_withValidBooking_savesPendingBookingPublishesEventIncrementsCounterAndExposesGauge() {
    when(userRepository.findById(borrowerId))
        .thenReturn(Optional.of(User.reconstitute(borrowerId, false)));
    when(bookingRepository.findOngoingByBorrowerId(borrowerId)).thenReturn(Optional.empty());
    when(carRepository.findById(carId)).thenReturn(Optional.of(Car.reconstitute(carId, "SEDAN")));
    when(bookingRepository.findOngoingByCarId(carId)).thenReturn(Optional.empty());
    when(bookingRepository.countByStatus(BookingStatus.PENDING)).thenReturn(1L);

    var booking = handler.handle(command);

    assertThat(booking.carId()).isEqualTo(carId);
    assertThat(booking.borrowerId()).isEqualTo(borrowerId);
    assertThat(booking.status()).isEqualTo(BookingStatus.PENDING);
    verify(bookingRepository).save(argThat(saved -> saved.status() == BookingStatus.PENDING));
    verify(publisher).publish(any(BookingPaymentRequested.class));
    assertThat(meterRegistry.counter("bookings.created.total").count()).isEqualTo(1.0);
    assertThat(meterRegistry.get("bookings.pending.current").gauge().value()).isEqualTo(1.0);
  }

  @Test
  void handle_withDebtorBorrower_throws() {
    when(userRepository.findById(borrowerId))
        .thenReturn(Optional.of(User.reconstitute(borrowerId, true)));

    assertThatThrownBy(() -> handler.handle(command)).isInstanceOf(IllegalArgumentException.class);
    verify(bookingRepository, never()).save(any());
    verify(publisher, never()).publish(any());
  }

  @Test
  void handle_withUnknownBorrower_throws() {
    when(userRepository.findById(borrowerId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> handler.handle(command)).isInstanceOf(IllegalArgumentException.class);
    verify(bookingRepository, never()).save(any());
  }

  @Test
  void handle_withOngoingBookingForBorrower_throws() {
    when(userRepository.findById(borrowerId))
        .thenReturn(Optional.of(User.reconstitute(borrowerId, false)));
    var existingBooking =
        Booking.reconstitute(
            UUID.randomUUID(),
            UUID.randomUUID(),
            borrowerId,
            BookingPeriod.of(start, end),
            Instant.now(),
            BookingStatus.ACTIVE);
    when(bookingRepository.findOngoingByBorrowerId(borrowerId))
        .thenReturn(Optional.of(existingBooking));

    assertThatThrownBy(() -> handler.handle(command)).isInstanceOf(IllegalArgumentException.class);
    verify(bookingRepository, never()).save(any());
  }

  @Test
  void handle_withUnknownCar_throws() {
    when(userRepository.findById(borrowerId))
        .thenReturn(Optional.of(User.reconstitute(borrowerId, false)));
    when(bookingRepository.findOngoingByBorrowerId(borrowerId)).thenReturn(Optional.empty());
    when(carRepository.findById(carId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> handler.handle(command)).isInstanceOf(IllegalArgumentException.class);
    verify(bookingRepository, never()).save(any());
  }

  @Test
  void handle_withUnavailableCar_throws() {
    when(userRepository.findById(borrowerId))
        .thenReturn(Optional.of(User.reconstitute(borrowerId, false)));
    when(bookingRepository.findOngoingByBorrowerId(borrowerId)).thenReturn(Optional.empty());
    when(carRepository.findById(carId)).thenReturn(Optional.of(Car.reconstitute(carId, "SEDAN")));
    var ongoingBooking =
        Booking.reconstitute(
            UUID.randomUUID(),
            carId,
            UUID.randomUUID(),
            BookingPeriod.of(start, end),
            Instant.now(),
            BookingStatus.PENDING);
    when(bookingRepository.findOngoingByCarId(carId)).thenReturn(Optional.of(ongoingBooking));

    assertThatThrownBy(() -> handler.handle(command)).isInstanceOf(IllegalArgumentException.class);
    verify(bookingRepository, never()).save(any());
  }
}
