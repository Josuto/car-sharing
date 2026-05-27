package com.example.cs.booking.infrastructure;

import com.example.cs.booking.domain.User;
import com.example.cs.booking.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class UserJpaAdapter implements UserRepository {

  private final UserJpaRepository jpaRepository;

  @Override
  public void save(User user) {
    jpaRepository.save(UserJpaEntity.from(user));
  }
}
