package com.banking.transactionservice.service;

import com.banking.transactionservice.dto.TransactionDto;
import com.banking.transactionservice.entity.TransactionStatus;
import com.banking.transactionservice.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    
    TransactionDto createTransaction(TransactionDto transactionDto);
    
    TransactionDto getTransactionById(Long id);
    
    TransactionDto getTransactionByTransactionId(String transactionId);
    
    List<TransactionDto> getTransactionsByFromAccountId(Long fromAccountId);
    
    List<TransactionDto> getTransactionsByToAccountId(Long toAccountId);
    
    List<TransactionDto> getTransactionsByAccountId(Long accountId);
    
    List<TransactionDto> getTransactionsByType(TransactionType transactionType);
    
    List<TransactionDto> getTransactionsByStatus(TransactionStatus status);
    
    List<TransactionDto> getTransactionsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount);
    
    List<TransactionDto> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    List<TransactionDto> getTransactionsByReferenceNumber(String referenceNumber);
    
    TransactionDto updateTransactionStatus(Long id, TransactionStatus status);
    
    TransactionDto updateTransactionStatusByTransactionId(String transactionId, TransactionStatus status);
    
    void deleteTransaction(Long id);
    
    boolean existsByTransactionId(String transactionId);
    
    long countTransactionsByFromAccountIdAndStatus(Long accountId, TransactionStatus status);
    
    long countTransactionsByToAccountIdAndStatus(Long accountId, TransactionStatus status);
    
    String generateTransactionId();
    
    String generateReferenceNumber();
}
