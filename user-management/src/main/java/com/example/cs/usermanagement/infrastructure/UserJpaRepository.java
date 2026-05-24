package com.example.cs.usermanagement.infrastructure;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {
  Optional<UserJpaEntity> findByUsernameAndIsDeletedFalse(String username);
}
