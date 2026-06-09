package com.example.cs.booking.application;

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
import com.example.cs.booking.domain.User;
import com.example.cs.booking.domain.UserRepository;
import com.example.cs.common.BorrowerFlaggedAsDebtor;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FlagLateReturnDebtorsHandlerTest {

  private final BookingRepository bookingRepository = mock(BookingRepository.class);
  private final UserRepository userRepository = mock(UserRepository.class);
  private final BookingEventPublisher publisher = mock(BookingEventPublisher.class);
  private final FlagLateReturnDebtorsHandler handler =
      new FlagLateReturnDebtorsHandler(bookingRepository, userRepository, publisher);

  private final UUID borrowerId = UUID.randomUUID();
  private final BookingPeriod overduePeriod =
      BookingPeriod.reconstitute(LocalDate.now().minusDays(3), LocalDate.now().minusDays(1));

  @Test
  void handle_withNoOverdueBookings_doesNothing() {
    when(bookingRepository.findOverdueActive()).thenReturn(List.of());

    handler.handle();

    verify(userRepository, never()).save(any());
    verify(publisher, never()).publish(any());
  }

  @Test
  void handle_withOverdueBookingAndNonDebtorBorrower_savesUserAndPublishesEvent() {
    var booking =
        Booking.reconstitute(
            UUID.randomUUID(), UUID.randomUUID(), borrowerId, overduePeriod, BookingStatus.ACTIVE);
    when(bookingRepository.findOverdueActive()).thenReturn(List.of(booking));
    when(userRepository.findById(borrowerId))
        .thenReturn(Optional.of(User.reconstitute(borrowerId, false)));

    handler.handle();

    verify(userRepository)
        .save(argThat(saved -> saved.isDebtor() && saved.id().equals(borrowerId)));
    verify(publisher)
        .publish(
            argThat(
                event ->
                    event instanceof BorrowerFlaggedAsDebtor b
                        && b.userId().equals(borrowerId.toString())));
  }

  @Test
  void handle_withOverdueBookingAndAlreadyDebtorBorrower_skips() {
    var booking =
        Booking.reconstitute(
            UUID.randomUUID(), UUID.randomUUID(), borrowerId, overduePeriod, BookingStatus.ACTIVE);
    when(bookingRepository.findOverdueActive()).thenReturn(List.of(booking));
    when(userRepository.findById(borrowerId))
        .thenReturn(Optional.of(User.reconstitute(borrowerId, true)));

    handler.handle();

    verify(userRepository, never()).save(any());
    verify(publisher, never()).publish(any());
  }

  @Test
  void handle_withMultipleOverdueBookings_onlyProcessesNonDebtors() {
    var nonDebtorId = UUID.randomUUID();
    var debtorId = UUID.randomUUID();
    var booking1 =
        Booking.reconstitute(
            UUID.randomUUID(), UUID.randomUUID(), nonDebtorId, overduePeriod, BookingStatus.ACTIVE);
    var booking2 =
        Booking.reconstitute(
            UUID.randomUUID(), UUID.randomUUID(), debtorId, overduePeriod, BookingStatus.ACTIVE);
    when(bookingRepository.findOverdueActive()).thenReturn(List.of(booking1, booking2));
    when(userRepository.findById(nonDebtorId))
        .thenReturn(Optional.of(User.reconstitute(nonDebtorId, false)));
    when(userRepository.findById(debtorId))
        .thenReturn(Optional.of(User.reconstitute(debtorId, true)));

    handler.handle();

    verify(userRepository).save(argThat(saved -> saved.id().equals(nonDebtorId)));
    verify(publisher)
        .publish(
            argThat(
                event ->
                    event instanceof BorrowerFlaggedAsDebtor b
                        && b.userId().equals(nonDebtorId.toString())));
  }
}
