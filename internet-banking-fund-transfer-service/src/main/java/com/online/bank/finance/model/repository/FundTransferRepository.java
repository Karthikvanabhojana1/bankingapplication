package com.online.bank.finance.model.repository;

import com.online.bank.finance.model.entity.FundTransferEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FundTransferRepository extends JpaRepository<FundTransferEntity, Long> {
}
