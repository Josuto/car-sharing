package com.example.cs.booking.infrastructure;

import com.example.cs.booking.domain.User;
import com.example.cs.booking.domain.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class UserJpaAdapter implements UserRepository {

  private final UserJpaRepository jpaRepository;

  @Override
  public Optional<User> findById(UUID id) {
    return jpaRepository.findById(id.toString()).map(UserJpaEntity::toDomain);
  }

  @Override
  public void save(User user) {
    jpaRepository.save(UserJpaEntity.from(user));
  }
}
