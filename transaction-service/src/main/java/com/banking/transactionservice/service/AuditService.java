package com.banking.transactionservice.service;

import com.banking.transactionservice.dto.TransactionDto;
import com.banking.transactionservice.entity.TransactionStatus;

public interface AuditService {
    
    void logTransactionCreated(TransactionDto transaction);
    
    void logTransactionStatusChanged(Long transactionId, TransactionStatus oldStatus, TransactionStatus newStatus);
    
    void logTransactionDeleted(Long transactionId);
    
    void logTransactionError(Long transactionId, String errorMessage);
    
    void logTransactionRetry(Long transactionId, int attemptNumber);
}
