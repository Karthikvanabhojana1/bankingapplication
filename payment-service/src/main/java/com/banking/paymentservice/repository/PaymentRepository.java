package com.banking.paymentservice.repository;

import com.banking.paymentservice.entity.Payment;
import com.banking.paymentservice.entity.PaymentStatus;
import com.banking.paymentservice.entity.PaymentType;
import com.banking.paymentservice.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByPaymentId(String paymentId);
    
    List<Payment> findByFromAccountId(Long fromAccountId);
    
    List<Payment> findByToAccountId(Long toAccountId);
    
    List<Payment> findByFromAccountIdOrToAccountId(Long fromAccountId, Long toAccountId);
    
    List<Payment> findByPaymentType(PaymentType paymentType);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);
    
    List<Payment> findByFromAccountIdAndStatus(Long fromAccountId, PaymentStatus status);
    
    List<Payment> findByToAccountIdAndStatus(Long toAccountId, PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.amount >= :minAmount")
    List<Payment> findByAmountGreaterThanOrEqualTo(@Param("minAmount") BigDecimal minAmount);
    
    @Query("SELECT p FROM Payment p WHERE p.amount <= :maxAmount")
    List<Payment> findByAmountLessThanOrEqualTo(@Param("maxAmount") BigDecimal maxAmount);
    
    @Query("SELECT p FROM Payment p WHERE p.amount BETWEEN :minAmount AND :maxAmount")
    List<Payment> findByAmountBetween(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);
    
    @Query("SELECT p FROM Payment p WHERE p.createdAt >= :startDate AND p.createdAt <= :endDate")
    List<Payment> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p FROM Payment p WHERE p.fromAccountId = :accountId OR p.toAccountId = :accountId ORDER BY p.createdAt DESC")
    List<Payment> findPaymentsByAccountId(@Param("accountId") Long accountId);
    
    @Query("SELECT p FROM Payment p WHERE p.referenceNumber = :referenceNumber")
    List<Payment> findByReferenceNumber(@Param("referenceNumber") String referenceNumber);
    
    boolean existsByPaymentId(String paymentId);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.fromAccountId = :accountId AND p.status = :status")
    long countByFromAccountIdAndStatus(@Param("accountId") Long accountId, @Param("status") PaymentStatus status);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.toAccountId = :accountId AND p.status = :status")
    long countByToAccountIdAndStatus(@Param("accountId") Long accountId, @Param("status") PaymentStatus status);
}
