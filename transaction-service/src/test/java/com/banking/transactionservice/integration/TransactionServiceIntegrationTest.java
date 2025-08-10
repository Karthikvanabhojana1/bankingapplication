package com.banking.transactionservice.integration;

import com.banking.transactionservice.dto.TransactionDto;
import com.banking.transactionservice.entity.TransactionStatus;
import com.banking.transactionservice.entity.TransactionType;
import com.banking.transactionservice.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransactionServiceIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    private TransactionDto transferTransaction;
    private TransactionDto depositTransaction;
    private TransactionDto withdrawalTransaction;

    @BeforeEach
    void setUp() {
        // Setup test data
        transferTransaction = new TransactionDto();
        transferTransaction.setFromAccountId(1L);
        transferTransaction.setToAccountId(2L);
        transferTransaction.setAmount(new BigDecimal("100.00"));
        transferTransaction.setTransactionType(TransactionType.TRANSFER);
        transferTransaction.setDescription("Test transfer transaction");

        depositTransaction = new TransactionDto();
        depositTransaction.setToAccountId(1L);
        depositTransaction.setAmount(new BigDecimal("500.00"));
        depositTransaction.setTransactionType(TransactionType.DEPOSIT);
        depositTransaction.setDescription("Test deposit transaction");

        withdrawalTransaction = new TransactionDto();
        withdrawalTransaction.setFromAccountId(1L);
        withdrawalTransaction.setAmount(new BigDecimal("50.00"));
        withdrawalTransaction.setTransactionType(TransactionType.WITHDRAWAL);
        withdrawalTransaction.setDescription("Test withdrawal transaction");
    }

    @Test
    void testCreateTransferTransaction_Success() {
        // Act
        TransactionDto result = transactionService.createTransaction(transferTransaction);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getTransactionId());
        assertTrue(result.getTransactionId().startsWith("TXN"));
        assertEquals(TransactionType.TRANSFER, result.getTransactionType());
        assertEquals(TransactionStatus.PENDING, result.getStatus());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        assertEquals(1L, result.getFromAccountId());
        assertEquals(2L, result.getToAccountId());
        assertNotNull(result.getReferenceNumber());
        assertEquals(10, result.getReferenceNumber().length());
    }

    @Test
    void testCreateDepositTransaction_Success() {
        // Act
        TransactionDto result = transactionService.createTransaction(depositTransaction);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionType.DEPOSIT, result.getTransactionType());
        assertEquals(TransactionStatus.PENDING, result.getStatus());
        assertEquals(new BigDecimal("500.00"), result.getAmount());
        assertEquals(1L, result.getToAccountId());
        assertNull(result.getFromAccountId());
    }

    @Test
    void testCreateWithdrawalTransaction_Success() {
        // Act
        TransactionDto result = transactionService.createTransaction(withdrawalTransaction);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionType.WITHDRAWAL, result.getTransactionType());
        assertEquals(TransactionStatus.PENDING, result.getStatus());
        assertEquals(new BigDecimal("50.00"), result.getAmount());
        assertEquals(1L, result.getFromAccountId());
        assertNull(result.getToAccountId());
    }

    @Test
    void testCreateTransactionWithCustomTransactionId_Success() {
        // Arrange
        transferTransaction.setTransactionId("CUSTOM_TXN_123");

        // Act
        TransactionDto result = transactionService.createTransaction(transferTransaction);

        // Assert
        assertEquals("CUSTOM_TXN_123", result.getTransactionId());
    }

    @Test
    void testCreateTransactionWithCustomReferenceNumber_Success() {
        // Arrange
        transferTransaction.setReferenceNumber("REF123456");

        // Act
        TransactionDto result = transactionService.createTransaction(transferTransaction);

        // Assert
        assertEquals("REF123456", result.getReferenceNumber());
    }

    @Test
    void testCreateTransaction_DuplicateTransactionId_ThrowsException() {
        // Arrange
        TransactionDto firstTransaction = transactionService.createTransaction(transferTransaction);
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(transferTransaction);
        });
    }

    @Test
    void testGetTransactionById_Success() {
        // Arrange
        TransactionDto created = transactionService.createTransaction(transferTransaction);

        // Act
        TransactionDto result = transactionService.getTransactionById(created.getId());

        // Assert
        assertNotNull(result);
        assertEquals(created.getId(), result.getId());
        assertEquals(created.getTransactionId(), result.getTransactionId());
    }

    @Test
    void testGetTransactionById_NotFound_ThrowsException() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            transactionService.getTransactionById(999L);
        });
    }

    @Test
    void testGetTransactionByTransactionId_Success() {
        // Arrange
        TransactionDto created = transactionService.createTransaction(transferTransaction);

        // Act
        TransactionDto result = transactionService.getTransactionByTransactionId(created.getTransactionId());

        // Assert
        assertNotNull(result);
        assertEquals(created.getTransactionId(), result.getTransactionId());
    }

    @Test
    void testGetTransactionsByFromAccountId_Success() {
        // Arrange
        transactionService.createTransaction(transferTransaction);
        transactionService.createTransaction(withdrawalTransaction);

        // Act
        List<TransactionDto> results = transactionService.getTransactionsByFromAccountId(1L);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(t -> t.getFromAccountId().equals(1L)));
    }

    @Test
    void testGetTransactionsByToAccountId_Success() {
        // Arrange
        transactionService.createTransaction(transferTransaction);
        transactionService.createTransaction(depositTransaction);

        // Act
        List<TransactionDto> results = transactionService.getTransactionsByToAccountId(1L);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size()); // Only deposit transaction has toAccountId = 1L
        assertTrue(results.stream().anyMatch(t -> t.getToAccountId().equals(1L)));
    }

    @Test
    void testGetTransactionsByType_Success() {
        // Arrange
        transactionService.createTransaction(transferTransaction);
        transactionService.createTransaction(depositTransaction);

        // Act
        List<TransactionDto> results = transactionService.getTransactionsByType(TransactionType.TRANSFER);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(TransactionType.TRANSFER, results.get(0).getTransactionType());
    }

    @Test
    void testGetTransactionsByStatus_Success() {
        // Arrange
        transactionService.createTransaction(transferTransaction);

        // Act
        List<TransactionDto> results = transactionService.getTransactionsByStatus(TransactionStatus.PENDING);

        // Assert
        assertNotNull(results);
        assertTrue(results.size() >= 1);
        assertTrue(results.stream().anyMatch(t -> t.getStatus().equals(TransactionStatus.PENDING)));
    }

    @Test
    void testUpdateTransactionStatus_Success() {
        // Arrange
        TransactionDto created = transactionService.createTransaction(transferTransaction);

        // Act
        TransactionDto result = transactionService.updateTransactionStatus(created.getId(), TransactionStatus.COMPLETED);

        // Assert
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
    }

    @Test
    void testUpdateTransactionStatusByTransactionId_Success() {
        // Arrange
        TransactionDto created = transactionService.createTransaction(transferTransaction);

        // Act
        TransactionDto result = transactionService.updateTransactionStatusByTransactionId(
            created.getTransactionId(), TransactionStatus.FAILED);

        // Assert
        assertEquals(TransactionStatus.FAILED, result.getStatus());
    }

    @Test
    void testGenerateTransactionId_Success() {
        // Act
        String transactionId = transactionService.generateTransactionId();

        // Assert
        assertNotNull(transactionId);
        assertTrue(transactionId.startsWith("TXN"));
        assertTrue(transactionId.length() > 10);
    }

    @Test
    void testGenerateReferenceNumber_Success() {
        // Act
        String referenceNumber = transactionService.generateReferenceNumber();

        // Assert
        assertNotNull(referenceNumber);
        assertEquals(10, referenceNumber.length());
        assertTrue(referenceNumber.matches("\\d{10}"));
    }

    @Test
    void testGetTransactionsByAmountRange_Success() {
        // Arrange
        transactionService.createTransaction(transferTransaction); // 100.00
        transactionService.createTransaction(depositTransaction);  // 500.00

        // Act
        List<TransactionDto> results = transactionService.getTransactionsByAmountRange(
            new BigDecimal("50.00"), new BigDecimal("200.00"));

        // Assert
        assertNotNull(results);
        assertTrue(results.size() >= 1);
        assertTrue(results.stream().allMatch(t -> 
            t.getAmount().compareTo(new BigDecimal("50.00")) >= 0 && 
            t.getAmount().compareTo(new BigDecimal("200.00")) <= 0));
    }

    @Test
    void testExistsByTransactionId_Success() {
        // Arrange
        TransactionDto created = transactionService.createTransaction(transferTransaction);

        // Act
        boolean exists = transactionService.existsByTransactionId(created.getTransactionId());

        // Assert
        assertTrue(exists);
    }

    @Test
    void testCountTransactionsByFromAccountIdAndStatus_Success() {
        // Arrange
        transactionService.createTransaction(transferTransaction);
        transactionService.createTransaction(withdrawalTransaction);

        // Act
        long count = transactionService.countTransactionsByFromAccountIdAndStatus(1L, TransactionStatus.PENDING);

        // Assert
        assertEquals(2, count);
    }
}
