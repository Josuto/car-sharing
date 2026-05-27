package com.example.cs.booking.infrastructure;

import com.example.cs.booking.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
  private boolean isDebtor;

  static UserJpaEntity from(User user) {
    return new UserJpaEntity(user.id().toString(), user.isDebtor());
  }

  User toDomain() {
    return User.reconstitute(UUID.fromString(id), isDebtor);
  }
}
