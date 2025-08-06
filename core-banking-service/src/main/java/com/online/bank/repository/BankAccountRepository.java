package com.online.bank.repository;

import com.online.bank.model.entity.BankAccountEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccountEntity, Long> {
    Optional<BankAccountEntity> findByNumber(String accountNumber);
}
