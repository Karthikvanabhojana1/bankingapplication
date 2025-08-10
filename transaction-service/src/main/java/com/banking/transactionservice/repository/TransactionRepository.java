package com.banking.transactionservice.repository;

import com.banking.transactionservice.entity.Transaction;
import com.banking.transactionservice.entity.TransactionStatus;
import com.banking.transactionservice.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Optional<Transaction> findByTransactionId(String transactionId);
    
    List<Transaction> findByFromAccountId(Long fromAccountId);
    
    List<Transaction> findByToAccountId(Long toAccountId);
    
    List<Transaction> findByFromAccountIdOrToAccountId(Long fromAccountId, Long toAccountId);
    
    List<Transaction> findByTransactionType(TransactionType transactionType);
    
    List<Transaction> findByStatus(TransactionStatus status);
    
    List<Transaction> findByFromAccountIdAndStatus(Long fromAccountId, TransactionStatus status);
    
    List<Transaction> findByToAccountIdAndStatus(Long toAccountId, TransactionStatus status);
    
    @Query("SELECT t FROM Transaction t WHERE t.amount >= :minAmount")
    List<Transaction> findByAmountGreaterThanOrEqualTo(@Param("minAmount") BigDecimal minAmount);
    
    @Query("SELECT t FROM Transaction t WHERE t.amount <= :maxAmount")
    List<Transaction> findByAmountLessThanOrEqualTo(@Param("maxAmount") BigDecimal maxAmount);
    
    @Query("SELECT t FROM Transaction t WHERE t.amount BETWEEN :minAmount AND :maxAmount")
    List<Transaction> findByAmountBetween(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);
    
    @Query("SELECT t FROM Transaction t WHERE t.createdAt >= :startDate AND t.createdAt <= :endDate")
    List<Transaction> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.fromAccountId = :accountId OR t.toAccountId = :accountId ORDER BY t.createdAt DESC")
    List<Transaction> findTransactionsByAccountId(@Param("accountId") Long accountId);
    
    @Query("SELECT t FROM Transaction t WHERE t.referenceNumber = :referenceNumber")
    List<Transaction> findByReferenceNumber(@Param("referenceNumber") String referenceNumber);
    
    boolean existsByTransactionId(String transactionId);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.fromAccountId = :accountId AND t.status = :status")
    long countByFromAccountIdAndStatus(@Param("accountId") Long accountId, @Param("status") TransactionStatus status);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.toAccountId = :accountId AND t.status = :status")
    long countByToAccountIdAndStatus(@Param("accountId") Long accountId, @Param("status") TransactionStatus status);
}
