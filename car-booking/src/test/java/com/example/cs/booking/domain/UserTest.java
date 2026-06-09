package com.example.cs.booking.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserTest {

  private final UUID userId = UUID.randomUUID();

  @Test
  void flagAsDebtor_returnsNewUserWithIsDebtorTrue() {
    var user = User.reconstitute(userId, false);

    var flagged = user.flagAsDebtor();

    assertThat(flagged.id()).isEqualTo(userId);
    assertThat(flagged.isDebtor()).isTrue();
  }
}
