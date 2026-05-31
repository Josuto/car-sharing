package com.example.cs.payments.infrastructure;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, String> {
  Optional<AccountJpaEntity> findByUserId(String userId);
}
