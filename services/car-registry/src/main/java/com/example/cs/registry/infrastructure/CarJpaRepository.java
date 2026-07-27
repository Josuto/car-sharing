package com.example.cs.registry.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

interface CarJpaRepository extends JpaRepository<CarJpaEntity, String> {
  boolean existsByRegistrationNumber(String registrationNumber);
}
