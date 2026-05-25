package com.example.cs.usermanagement.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.example.cs.common.BorrowerFlaggedAsDebtor;
import com.example.cs.common.UserCreated;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  void create_withValidData_returnsActiveNonDeletedNonDebtorUserAndRaisesUserCreatedEvent() {
    var user = User.create(UUID.randomUUID(), "johndoe", "John", "Doe");

    assertThat(user.username()).isEqualTo("johndoe");
    assertThat(user.name()).isEqualTo("John");
    assertThat(user.surname()).isEqualTo("Doe");
    assertThat(user.isDebtor()).isFalse();
    assertThat(user.isDeleted()).isFalse();
    assertThat(user.pullDomainEvents()).singleElement().isInstanceOf(UserCreated.class);
  }

  @Test
  void create_withBlankUsername_throws() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> User.create(UUID.randomUUID(), " ", "John", "Doe"));
  }

  @Test
  void create_withBlankName_throws() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> User.create(UUID.randomUUID(), "johndoe", " ", "Doe"));
  }

  @Test
  void create_withBlankSurname_throws() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> User.create(UUID.randomUUID(), "johndoe", "John", " "));
  }

  @Test
  void flagAsDebtor_setsDebtorAndRaisesBorrowerFlaggedAsDebtorEvent() {
    var user = User.reconstitute(UUID.randomUUID(), "johndoe", "John", "Doe", false, false);

    user.flagAsDebtor();

    assertThat(user.isDebtor()).isTrue();
    assertThat(user.pullDomainEvents()).singleElement().isInstanceOf(BorrowerFlaggedAsDebtor.class);
  }
}
