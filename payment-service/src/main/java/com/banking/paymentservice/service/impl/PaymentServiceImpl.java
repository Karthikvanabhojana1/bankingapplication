package com.banking.paymentservice.service.impl;

import com.banking.paymentservice.dto.PaymentDto;
import com.banking.paymentservice.entity.Payment;
import com.banking.paymentservice.entity.PaymentStatus;
import com.banking.paymentservice.entity.PaymentType;
import com.banking.paymentservice.repository.PaymentRepository;
import com.banking.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    @Transactional
    public PaymentDto createPayment(PaymentDto paymentDto) {
        Payment payment = toEntity(paymentDto);
        Payment saved = paymentRepository.save(payment);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        return toDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentByPaymentId(String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found with paymentId: " + paymentId));
        return toDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByFromAccount(Long fromAccountId) {
        return paymentRepository.findByFromAccountId(fromAccountId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByToAccount(Long toAccountId) {
        return paymentRepository.findByToAccountId(toAccountId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByAccount(Long accountId) {
        return paymentRepository.findByFromAccountIdOrToAccountId(accountId, accountId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByType(PaymentType type) {
        return paymentRepository.findByPaymentType(type).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        return paymentRepository.findByAmountBetween(minAmount, maxAmount).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByDateRange(startDate, endDate).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByReferenceNumber(String referenceNumber) {
        return paymentRepository.findByReferenceNumber(referenceNumber).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentDto updatePaymentStatus(Long id, PaymentStatus status) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        payment.setStatus(status);
        Payment updated = paymentRepository.save(payment);
        return toDto(updated);
    }

    private PaymentDto toDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setPaymentId(payment.getPaymentId());
        dto.setFromAccountId(payment.getFromAccountId());
        dto.setToAccountId(payment.getToAccountId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentType(payment.getPaymentType());
        dto.setStatus(payment.getStatus());
        dto.setDescription(payment.getDescription());
        dto.setReferenceNumber(payment.getReferenceNumber());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        return dto;
    }

    private Payment toEntity(PaymentDto dto) {
        Payment payment = new Payment();
        payment.setPaymentId(dto.getPaymentId());
        payment.setFromAccountId(dto.getFromAccountId());
        payment.setToAccountId(dto.getToAccountId());
        payment.setAmount(dto.getAmount());
        payment.setPaymentType(dto.getPaymentType());
        payment.setStatus(dto.getStatus());
        payment.setDescription(dto.getDescription());
        payment.setReferenceNumber(dto.getReferenceNumber());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setCreatedAt(dto.getCreatedAt());
        payment.setUpdatedAt(dto.getUpdatedAt());
        return payment;
    }
}


