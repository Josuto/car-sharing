package com.example.cs.payments.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

interface TransactionJpaRepository extends JpaRepository<TransactionJpaEntity, String> {}
