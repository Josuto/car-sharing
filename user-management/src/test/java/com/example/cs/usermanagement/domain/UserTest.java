package com.example.cs.usermanagement.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.example.cs.common.UserBankAccountChanged;
import com.example.cs.common.UserCreated;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  void create_withValidData_returnsActiveNonDeletedNonDebtorUserAndRaisesUserCreatedEvent() {
    var user = User.create(UUID.randomUUID(), "johndoe", "John", "Doe", "ES1234567890");

    assertThat(user.username()).isEqualTo("johndoe");
    assertThat(user.name()).isEqualTo("John");
    assertThat(user.surname()).isEqualTo("Doe");
    assertThat(user.bankAccount()).isEqualTo("ES1234567890");
    assertThat(user.isDebtor()).isFalse();
    assertThat(user.isDeleted()).isFalse();
    assertThat(user.pullDomainEvents()).singleElement().isInstanceOf(UserCreated.class);
  }

  @Test
  void create_withBlankUsername_throws() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> User.create(UUID.randomUUID(), " ", "John", "Doe", "ES1234567890"));
  }

  @Test
  void create_withBlankName_throws() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> User.create(UUID.randomUUID(), "johndoe", " ", "Doe", "ES1234567890"));
  }

  @Test
  void create_withBlankSurname_throws() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> User.create(UUID.randomUUID(), "johndoe", "John", " ", "ES1234567890"));
  }

  @Test
  void create_withBlankBankAccount_throws() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> User.create(UUID.randomUUID(), "johndoe", "John", "Doe", " "));
  }

  @Test
  void update_withNewBankAccount_emitsUserBankAccountChangedEvent() {
    var user =
        User.reconstitute(
            UUID.randomUUID(), "johndoe", "John", "Doe", "ES1234567890", false, false);

    user.update("John", "Doe", "ES0987654321");

    assertThat(user.bankAccount()).isEqualTo("ES0987654321");
    assertThat(user.pullDomainEvents()).singleElement().isInstanceOf(UserBankAccountChanged.class);
  }

  @Test
  void update_withSameBankAccount_emitsNoEvent() {
    var user =
        User.reconstitute(
            UUID.randomUUID(), "johndoe", "John", "Doe", "ES1234567890", false, false);

    user.update("Jane", "Smith", "ES1234567890");

    assertThat(user.name()).isEqualTo("Jane");
    assertThat(user.surname()).isEqualTo("Smith");
    assertThat(user.pullDomainEvents()).isEmpty();
  }

  @Test
  void update_withBlankBankAccount_throws() {
    var user =
        User.reconstitute(
            UUID.randomUUID(), "johndoe", "John", "Doe", "ES1234567890", false, false);

    assertThatIllegalArgumentException().isThrownBy(() -> user.update("John", "Doe", " "));
  }

  @Test
  void flagAsDebtor_setsIsDebtorTrue() {
    var user =
        User.reconstitute(
            UUID.randomUUID(), "johndoe", "John", "Doe", "ES1234567890", false, false);

    user.flagAsDebtor();

    assertThat(user.isDebtor()).isTrue();
    assertThat(user.pullDomainEvents()).isEmpty();
  }
}
