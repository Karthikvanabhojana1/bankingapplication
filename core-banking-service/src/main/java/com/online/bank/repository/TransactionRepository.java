package com.online.bank.repository;

import com.online.bank.model.entity.TransactionEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
}
