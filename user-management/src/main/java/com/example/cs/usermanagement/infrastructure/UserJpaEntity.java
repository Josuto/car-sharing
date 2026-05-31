package com.example.cs.usermanagement.infrastructure;

import com.example.cs.usermanagement.domain.User;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
class UserJpaEntity {

  @Id private String id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String surname;

  @Column(nullable = false)
  private String bankAccount;

  @Column(nullable = false)
  private boolean isDebtor;

  @Column(nullable = false)
  private boolean isDeleted;

  static UserJpaEntity from(User user) {
    return new UserJpaEntity(
        user.id().toString(),
        user.username(),
        user.name(),
        user.surname(),
        user.bankAccount(),
        user.isDebtor(),
        user.isDeleted());
  }

  User toDomain() {
    return User.reconstitute(
        UUID.fromString(id), username, name, surname, bankAccount, isDebtor, isDeleted);
  }
}
