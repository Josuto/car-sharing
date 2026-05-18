package com.example.cs.usermanagement.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {
    Optional<UserJpaEntity> findByUsernameAndIsDeletedFalse(String username);
}
