package com.example.cs.usermanagement.infrastructure;

import com.example.cs.usermanagement.domain.User;
import com.example.cs.usermanagement.domain.UserRepository;
import java.util.Optional;
import java.util.UUID;
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

  @Override
  public Optional<User> findById(UUID id) {
    return jpaRepository.findById(id.toString()).map(UserJpaEntity::toDomain);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return jpaRepository.findByUsernameAndIsDeletedFalse(username).map(UserJpaEntity::toDomain);
  }

  @Override
  public long countDebtors() {
    return jpaRepository.countByIsDebtorTrueAndIsDeletedFalse();
  }
}
