package com.example.cs.booking.application;

import com.example.cs.booking.domain.Booking;
import com.example.cs.booking.domain.BookingEventPublisher;
import com.example.cs.booking.domain.BookingPeriod;
import com.example.cs.booking.domain.BookingRepository;
import com.example.cs.booking.domain.BookingStatus;
import com.example.cs.booking.domain.CarRepository;
import com.example.cs.booking.domain.UserRepository;
import com.example.cs.common.BookingPaymentRequested;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.UUID;

public class CreateBookingHandler implements CreateBookingUseCase {

  private final CarRepository carRepository;
  private final UserRepository userRepository;
  private final BookingRepository bookingRepository;
  private final BookingEventPublisher publisher;
  private final MeterRegistry meterRegistry;

  public CreateBookingHandler(
      CarRepository carRepository,
      UserRepository userRepository,
      BookingRepository bookingRepository,
      BookingEventPublisher publisher,
      MeterRegistry meterRegistry) {
    this.carRepository = carRepository;
    this.userRepository = userRepository;
    this.bookingRepository = bookingRepository;
    this.publisher = publisher;
    this.meterRegistry = meterRegistry;
    Gauge.builder(
            "bookings.pending.current",
            bookingRepository,
            r -> r.countByStatus(BookingStatus.PENDING))
        .register(meterRegistry);
  }

  @Override
  public Booking handle(CreateBookingCommand command) {
    var borrower =
        userRepository
            .findById(command.borrowerId())
            .orElseThrow(() -> new IllegalArgumentException("borrower not found"));

    if (borrower.isDebtor()) {
      throw new IllegalArgumentException("borrower is a debtor");
    }

    bookingRepository
        .findOngoingByBorrowerId(command.borrowerId())
        .ifPresent(
            ongoing -> {
              throw new IllegalArgumentException("borrower already has an ongoing booking");
            });

    carRepository
        .findById(command.carId())
        .orElseThrow(() -> new IllegalArgumentException("car not found"));

    bookingRepository
        .findOngoingByCarId(command.carId())
        .ifPresent(
            ongoing -> {
              throw new IllegalArgumentException("car is not available");
            });

    var period = BookingPeriod.of(command.startDate(), command.endDate());
    var booking = Booking.create(UUID.randomUUID(), command.carId(), command.borrowerId(), period);
    bookingRepository.save(booking);

    publisher.publish(
        new BookingPaymentRequested(
            booking.id().toString(),
            command.borrowerId().toString(),
            command.carId().toString(),
            command.startDate(),
            command.endDate()));

    meterRegistry.counter("bookings.created.total").increment();
    return booking;
  }
}
