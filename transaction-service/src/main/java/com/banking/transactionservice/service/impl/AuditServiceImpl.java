package com.banking.transactionservice.service.impl;

import com.banking.transactionservice.dto.TransactionDto;
import com.banking.transactionservice.entity.TransactionStatus;
import com.banking.transactionservice.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AuditServiceImpl implements AuditService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void logTransactionCreated(TransactionDto transaction) {
        String timestamp = LocalDateTime.now().format(formatter);
        logger.info("[AUDIT] [{}] Transaction CREATED - ID: {}, TransactionID: {}, Amount: {}, Type: {}, Status: {}",
            timestamp, transaction.getId(), transaction.getTransactionId(), 
            transaction.getAmount(), transaction.getTransactionType(), transaction.getStatus());
    }
    
    @Override
    public void logTransactionStatusChanged(Long transactionId, TransactionStatus oldStatus, TransactionStatus newStatus) {
        String timestamp = LocalDateTime.now().format(formatter);
        logger.info("[AUDIT] [{}] Transaction STATUS CHANGED - TransactionID: {}, Old Status: {}, New Status: {}",
            timestamp, transactionId, oldStatus, newStatus);
    }
    
    @Override
    public void logTransactionDeleted(Long transactionId) {
        String timestamp = LocalDateTime.now().format(formatter);
        logger.info("[AUDIT] [{}] Transaction DELETED - TransactionID: {}", timestamp, transactionId);
    }
    
    @Override
    public void logTransactionError(Long transactionId, String errorMessage) {
        String timestamp = LocalDateTime.now().format(formatter);
        logger.error("[AUDIT] [{}] Transaction ERROR - TransactionID: {}, Error: {}", 
            timestamp, transactionId, errorMessage);
    }
    
    @Override
    public void logTransactionRetry(Long transactionId, int attemptNumber) {
        String timestamp = LocalDateTime.now().format(formatter);
        logger.warn("[AUDIT] [{}] Transaction RETRY - TransactionID: {}, Attempt: {}", 
            timestamp, transactionId, attemptNumber);
    }
}
