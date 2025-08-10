package com.banking.transactionservice.service.impl;

import com.banking.transactionservice.dto.TransactionDto;
import com.banking.transactionservice.entity.Transaction;
import com.banking.transactionservice.entity.TransactionStatus;
import com.banking.transactionservice.entity.TransactionType;
import com.banking.transactionservice.repository.TransactionRepository;
import com.banking.transactionservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Override
    public TransactionDto createTransaction(TransactionDto transactionDto) {
        // Check if transaction ID already exists
        if (existsByTransactionId(transactionDto.getTransactionId())) {
            throw new RuntimeException("Transaction with ID " + transactionDto.getTransactionId() + " already exists");
        }
        
        // Generate transaction ID if not provided
        if (transactionDto.getTransactionId() == null || transactionDto.getTransactionId().isEmpty()) {
            transactionDto.setTransactionId(generateTransactionId());
        }
        
        // Generate reference number if not provided
        if (transactionDto.getReferenceNumber() == null || transactionDto.getReferenceNumber().isEmpty()) {
            transactionDto.setReferenceNumber(generateReferenceNumber());
        }
        
        Transaction transaction = new Transaction(
            transactionDto.getTransactionId(),
            transactionDto.getFromAccountId(),
            transactionDto.getToAccountId(),
            transactionDto.getAmount(),
            transactionDto.getTransactionType(),
            transactionDto.getDescription()
        );
        
        transaction.setReferenceNumber(transactionDto.getReferenceNumber());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDto(savedTransaction);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TransactionDto getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        return convertToDto(transaction);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TransactionDto getTransactionByTransactionId(String transactionId) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));
        return convertToDto(transaction);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionsByFromAccountId(Long fromAccountId) {
        return transactionRepository.findByFromAccountId(fromAccountId).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionsByToAccountId(Long toAccountId) {
        return transactionRepository.findByToAccountId(toAccountId).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findTransactionsByAccountId(accountId).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionsByType(TransactionType transactionType) {
        return transactionRepository.findByTransactionType(transactionType).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionsByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        return transactionRepository.findByAmountBetween(minAmount, maxAmount).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByDateRange(startDate, endDate).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionsByReferenceNumber(String referenceNumber) {
        return transactionRepository.findByReferenceNumber(referenceNumber).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public TransactionDto updateTransactionStatus(Long id, TransactionStatus status) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        
        transaction.setStatus(status);
        Transaction updatedTransaction = transactionRepository.save(transaction);
        return convertToDto(updatedTransaction);
    }
    
    @Override
    public TransactionDto updateTransactionStatusByTransactionId(String transactionId, TransactionStatus status) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));
        
        transaction.setStatus(status);
        Transaction updatedTransaction = transactionRepository.save(transaction);
        return convertToDto(updatedTransaction);
    }
    
    @Override
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByTransactionId(String transactionId) {
        return transactionRepository.existsByTransactionId(transactionId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countTransactionsByFromAccountIdAndStatus(Long accountId, TransactionStatus status) {
        return transactionRepository.countByFromAccountIdAndStatus(accountId, status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countTransactionsByToAccountIdAndStatus(Long accountId, TransactionStatus status) {
        return transactionRepository.countByToAccountIdAndStatus(accountId, status);
    }
    
    @Override
    public String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    @Override
    public String generateReferenceNumber() {
        Random random = new Random();
        StringBuilder referenceNumber = new StringBuilder();
        
        // Generate 10-digit reference number
        for (int i = 0; i < 10; i++) {
            referenceNumber.append(random.nextInt(10));
        }
        
        String generatedReference = referenceNumber.toString();
        
        // Ensure uniqueness
        while (transactionRepository.findByReferenceNumber(generatedReference).size() > 0) {
            referenceNumber = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                referenceNumber.append(random.nextInt(10));
            }
            generatedReference = referenceNumber.toString();
        }
        
        return generatedReference;
    }
    
    private TransactionDto convertToDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setTransactionId(transaction.getTransactionId());
        dto.setFromAccountId(transaction.getFromAccountId());
        dto.setToAccountId(transaction.getToAccountId());
        dto.setAmount(transaction.getAmount());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setStatus(transaction.getStatus());
        dto.setDescription(transaction.getDescription());
        dto.setReferenceNumber(transaction.getReferenceNumber());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setUpdatedAt(transaction.getUpdatedAt());
        return dto;
    }
}
