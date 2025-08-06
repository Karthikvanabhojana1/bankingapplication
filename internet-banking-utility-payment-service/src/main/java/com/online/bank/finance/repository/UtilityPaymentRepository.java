package com.online.bank.finance.repository;

import com.online.bank.finance.model.dto.UtilityPayment;
import com.online.bank.finance.model.entity.UtilityPaymentEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilityPaymentRepository extends JpaRepository<UtilityPaymentEntity, UtilityPayment> {
}
