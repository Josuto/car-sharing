package com.example.cs.usermanagement.application;

import com.example.cs.usermanagement.domain.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class UpdateDebtorStatusHandler implements UpdateDebtorStatusUseCase {

  private static final Logger log = LoggerFactory.getLogger(UpdateDebtorStatusHandler.class);

  private final UserRepository userRepository;

  @Override
  public void handle(UpdateDebtorStatusCommand command) {
    var userId = UUID.fromString(command.userId());
    var userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      log.warn("Received BorrowerFlaggedAsDebtor for unknown userId={}", command.userId());
      return;
    }
    var user = userOpt.get();
    if (user.isDebtor()) {
      return;
    }
    user.flagAsDebtor();
    userRepository.save(user);
  }
}
