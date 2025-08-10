package com.banking.transactionservice.service;

import com.banking.transactionservice.entity.TransactionStatus;
import com.banking.transactionservice.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public interface TransactionStatisticsService {
    
    Map<TransactionType, Long> getTransactionCountByType(LocalDateTime startDate, LocalDateTime endDate);
    
    Map<TransactionStatus, Long> getTransactionCountByStatus(LocalDateTime startDate, LocalDateTime endDate);
    
    BigDecimal getTotalTransactionAmount(LocalDateTime startDate, LocalDateTime endDate);
    
    BigDecimal getAverageTransactionAmount(LocalDateTime startDate, LocalDateTime endDate);
    
    Map<String, BigDecimal> getTransactionAmountByAccount(Long accountId, LocalDateTime startDate, LocalDateTime endDate);
    
    Map<String, Long> getTransactionCountByAccount(Long accountId, LocalDateTime startDate, LocalDateTime endDate);
    
    Map<String, Object> getTransactionSummary(LocalDateTime startDate, LocalDateTime endDate);
}
