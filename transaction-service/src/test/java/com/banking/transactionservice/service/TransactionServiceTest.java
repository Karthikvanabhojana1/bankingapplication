package com.banking.transactionservice.service;

import com.banking.transactionservice.dto.TransactionDto;
import com.banking.transactionservice.entity.Transaction;
import com.banking.transactionservice.entity.TransactionStatus;
import com.banking.transactionservice.entity.TransactionType;
import com.banking.transactionservice.repository.TransactionRepository;
import com.banking.transactionservice.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private TransactionDto transactionDto;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transactionDto = new TransactionDto();
        transactionDto.setTransactionId("TXN123456789");
        transactionDto.setFromAccountId(1L);
        transactionDto.setToAccountId(2L);
        transactionDto.setAmount(new BigDecimal("100.00"));
        transactionDto.setTransactionType(TransactionType.TRANSFER);
        transactionDto.setDescription("Test transfer");

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setTransactionId("TXN123456789");
        transaction.setFromAccountId(1L);
        transaction.setToAccountId(2L);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setDescription("Test transfer");
    }

    @Test
    void testCreateTransaction_Success() {
        when(transactionRepository.existsByTransactionId(anyString())).thenReturn(false);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDto result = transactionService.createTransaction(transactionDto);

        assertNotNull(result);
        assertEquals(transactionDto.getTransactionId(), result.getTransactionId());
        assertEquals(transactionDto.getAmount(), result.getAmount());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_DuplicateTransactionId() {
        when(transactionRepository.existsByTransactionId(anyString())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> transactionService.createTransaction(transactionDto));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testGetTransactionById_Success() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        TransactionDto result = transactionService.getTransactionById(1L);

        assertNotNull(result);
        assertEquals(transaction.getTransactionId(), result.getTransactionId());
    }

    @Test
    void testGetTransactionById_NotFound() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transactionService.getTransactionById(1L));
    }

    @Test
    void testUpdateTransactionStatus_Success() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDto result = transactionService.updateTransactionStatus(1L, TransactionStatus.COMPLETED);

        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testGenerateTransactionId() {
        String transactionId = transactionService.generateTransactionId();
        
        assertNotNull(transactionId);
        assertTrue(transactionId.startsWith("TXN"));
        assertTrue(transactionId.length() > 10);
    }

    @Test
    void testGenerateReferenceNumber() {
        when(transactionRepository.findByReferenceNumber(anyString())).thenReturn(java.util.Collections.emptyList());
        
        String referenceNumber = transactionService.generateReferenceNumber();
        
        assertNotNull(referenceNumber);
        assertEquals(10, referenceNumber.length());
        assertTrue(referenceNumber.matches("\\d{10}"));
    }
}
