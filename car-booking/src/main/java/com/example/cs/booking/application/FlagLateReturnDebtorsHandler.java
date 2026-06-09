package com.example.cs.booking.application;

import com.example.cs.booking.domain.Booking;
import com.example.cs.booking.domain.BookingEventPublisher;
import com.example.cs.booking.domain.BookingRepository;
import com.example.cs.booking.domain.UserRepository;
import com.example.cs.common.BorrowerFlaggedAsDebtor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class FlagLateReturnDebtorsHandler {

  private static final Logger log = LoggerFactory.getLogger(FlagLateReturnDebtorsHandler.class);

  private final BookingRepository bookingRepository;
  private final UserRepository userRepository;
  private final BookingEventPublisher publisher;

  public void handle() {
    for (Booking booking : bookingRepository.findOverdueActive()) {
      var userOpt = userRepository.findById(booking.borrowerId());
      if (userOpt.isEmpty()) {
        log.warn(
            "Overdue booking {} has unknown borrowerId={}", booking.id(), booking.borrowerId());
        continue;
      }
      var user = userOpt.get();
      if (user.isDebtor()) {
        continue;
      }
      userRepository.save(user.flagAsDebtor());
      publisher.publish(new BorrowerFlaggedAsDebtor(booking.borrowerId().toString()));
    }
  }
}
