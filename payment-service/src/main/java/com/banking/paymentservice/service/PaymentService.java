package com.banking.paymentservice.service;

import com.banking.paymentservice.dto.PaymentDto;
import com.banking.paymentservice.entity.PaymentStatus;
import com.banking.paymentservice.entity.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {

    PaymentDto createPayment(PaymentDto paymentDto);

    PaymentDto getPaymentById(Long id);

    PaymentDto getPaymentByPaymentId(String paymentId);

    List<PaymentDto> getPaymentsByFromAccount(Long fromAccountId);

    List<PaymentDto> getPaymentsByToAccount(Long toAccountId);

    List<PaymentDto> getPaymentsByAccount(Long accountId);

    List<PaymentDto> getPaymentsByStatus(PaymentStatus status);

    List<PaymentDto> getPaymentsByType(PaymentType type);

    List<PaymentDto> getPaymentsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount);

    List<PaymentDto> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<PaymentDto> getPaymentsByReferenceNumber(String referenceNumber);

    PaymentDto updatePaymentStatus(Long id, PaymentStatus status);
}


