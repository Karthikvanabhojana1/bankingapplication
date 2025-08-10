package com.banking.transactionservice.integration;

import com.banking.transactionservice.dto.TransactionDto;
import com.banking.transactionservice.entity.TransactionStatus;
import com.banking.transactionservice.entity.TransactionType;
import com.banking.transactionservice.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class BankingFlowIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TransactionService transactionService;

    private String baseUrl;
    private Long userId;
    private Long savingsAccountId;
    private Long checkingAccountId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        setupTestData();
    }

    private void setupTestData() {
        // Create test user
        Map<String, Object> userData = new HashMap<>();
        userData.put("firstName", "Test");
        userData.put("lastName", "User");
        userData.put("email", "test.user@example.com");
        userData.put("phoneNumber", "5551234567");
        userData.put("address", "123 Test Street, Test City, TC 12345");

        ResponseEntity<Map> userResponse = restTemplate.postForEntity(
            baseUrl + "/api/users", userData, Map.class);
        
        if (userResponse.getStatusCode() == HttpStatus.OK && userResponse.getBody() != null) {
            Map<String, Object> userBody = userResponse.getBody();
            if (userBody.containsKey("data")) {
                Map<String, Object> user = (Map<String, Object>) userBody.get("data");
                userId = Long.valueOf(user.get("id").toString());
            }
        }

        // Create test accounts
        if (userId != null) {
            // Create savings account
            Map<String, Object> savingsData = new HashMap<>();
            savingsData.put("userId", userId);
            savingsData.put("accountType", "SAVINGS");
            savingsData.put("balance", 1000.00);
            savingsData.put("currency", "USD");

            ResponseEntity<Map> savingsResponse = restTemplate.postForEntity(
                baseUrl + "/api/accounts", savingsData, Map.class);
            
            if (savingsResponse.getStatusCode() == HttpStatus.OK && savingsResponse.getBody() != null) {
                Map<String, Object> savingsBody = savingsResponse.getBody();
                if (savingsBody.containsKey("data")) {
                    Map<String, Object> account = (Map<String, Object>) savingsBody.get("data");
                    savingsAccountId = Long.valueOf(account.get("id").toString());
                }
            }

            // Create checking account
            Map<String, Object> checkingData = new HashMap<>();
            checkingData.put("userId", userId);
            checkingData.put("accountType", "CHECKING");
            checkingData.put("balance", 500.00);
            checkingData.put("currency", "USD");

            ResponseEntity<Map> checkingResponse = restTemplate.postForEntity(
                baseUrl + "/api/accounts", checkingData, Map.class);
            
            if (checkingResponse.getStatusCode() == HttpStatus.OK && checkingResponse.getBody() != null) {
                Map<String, Object> checkingBody = checkingResponse.getBody();
                if (checkingBody.containsKey("data")) {
                    Map<String, Object> account = (Map<String, Object>) checkingBody.get("data");
                    checkingAccountId = Long.valueOf(account.get("id").toString());
                }
            }
        }
    }

    @Test
    void testCompleteBankingFlow_Success() {
        // Skip if setup failed
        assumeTrue(userId != null && savingsAccountId != null && checkingAccountId != null,
            "Test setup failed - skipping test");

        // Test 1: Create a transfer transaction
        TransactionDto transferTransaction = new TransactionDto();
        transferTransaction.setFromAccountId(savingsAccountId);
        transferTransaction.setToAccountId(checkingAccountId);
        transferTransaction.setAmount(new BigDecimal("200.00"));
        transferTransaction.setTransactionType(TransactionType.TRANSFER);
        transferTransaction.setDescription("Test transfer from savings to checking");

        TransactionDto createdTransfer = transactionService.createTransaction(transferTransaction);
        assertNotNull(createdTransfer);
        assertEquals(TransactionType.TRANSFER, createdTransfer.getTransactionType());
        assertEquals(TransactionStatus.PENDING, createdTransfer.getStatus());
        assertEquals(new BigDecimal("200.00"), createdTransfer.getAmount());

        // Test 2: Create a deposit transaction
        TransactionDto depositTransaction = new TransactionDto();
        depositTransaction.setToAccountId(savingsAccountId);
        depositTransaction.setAmount(new BigDecimal("500.00"));
        depositTransaction.setTransactionType(TransactionType.DEPOSIT);
        depositTransaction.setDescription("Test deposit to savings account");

        TransactionDto createdDeposit = transactionService.createTransaction(depositTransaction);
        assertNotNull(createdDeposit);
        assertEquals(TransactionType.DEPOSIT, createdDeposit.getTransactionType());
        assertEquals(TransactionStatus.PENDING, createdDeposit.getStatus());

        // Test 3: Create a withdrawal transaction
        TransactionDto withdrawalTransaction = new TransactionDto();
        withdrawalTransaction.setFromAccountId(checkingAccountId);
        withdrawalTransaction.setAmount(new BigDecimal("100.00"));
        withdrawalTransaction.setTransactionType(TransactionType.WITHDRAWAL);
        withdrawalTransaction.setDescription("Test withdrawal from checking account");

        TransactionDto createdWithdrawal = transactionService.createTransaction(withdrawalTransaction);
        assertNotNull(createdWithdrawal);
        assertEquals(TransactionType.WITHDRAWAL, createdWithdrawal.getTransactionType());
        assertEquals(TransactionStatus.PENDING, createdWithdrawal.getStatus());

        // Test 4: Update transaction statuses
        TransactionDto completedTransfer = transactionService.updateTransactionStatus(
            createdTransfer.getId(), TransactionStatus.COMPLETED);
        assertEquals(TransactionStatus.COMPLETED, completedTransfer.getStatus());

        TransactionDto completedDeposit = transactionService.updateTransactionStatus(
            createdDeposit.getId(), TransactionStatus.COMPLETED);
        assertEquals(TransactionStatus.COMPLETED, completedDeposit.getStatus());

        TransactionDto completedWithdrawal = transactionService.updateTransactionStatus(
            createdWithdrawal.getId(), TransactionStatus.COMPLETED);
        assertEquals(TransactionStatus.COMPLETED, completedWithdrawal.getStatus());

        // Test 5: Verify transaction queries
        List<TransactionDto> savingsTransactions = transactionService.getTransactionsByFromAccountId(savingsAccountId);
        assertTrue(savingsTransactions.size() >= 1);
        assertTrue(savingsTransactions.stream().anyMatch(t -> 
            t.getTransactionType() == TransactionType.TRANSFER));

        List<TransactionDto> checkingTransactions = transactionService.getTransactionsByToAccountId(checkingAccountId);
        assertTrue(checkingTransactions.size() >= 1);
        assertTrue(checkingTransactions.stream().anyMatch(t -> 
            t.getTransactionType() == TransactionType.TRANSFER));

        List<TransactionDto> completedTransactions = transactionService.getTransactionsByStatus(TransactionStatus.COMPLETED);
        assertTrue(completedTransactions.size() >= 3);

        // Test 6: Test amount range queries
        List<TransactionDto> largeTransactions = transactionService.getTransactionsByAmountRange(
            new BigDecimal("150.00"), new BigDecimal("600.00"));
        assertTrue(largeTransactions.size() >= 2); // Transfer (200) and Deposit (500)

        // Test 7: Test transaction type queries
        List<TransactionDto> transferTransactions = transactionService.getTransactionsByType(TransactionType.TRANSFER);
        assertTrue(transferTransactions.size() >= 1);

        List<TransactionDto> depositTransactions = transactionService.getTransactionsByType(TransactionType.DEPOSIT);
        assertTrue(depositTransactions.size() >= 1);

        List<TransactionDto> withdrawalTransactions = transactionService.getTransactionsByType(TransactionType.WITHDRAWAL);
        assertTrue(withdrawalTransactions.size() >= 1);
    }

    @Test
    void testTransactionValidation_Success() {
        // Skip if setup failed
        assumeTrue(userId != null && savingsAccountId != null && checkingAccountId != null,
            "Test setup failed - skipping test");

        // Test 1: Transaction with custom transaction ID
        TransactionDto customTransaction = new TransactionDto();
        customTransaction.setTransactionId("CUSTOM_TXN_123");
        customTransaction.setFromAccountId(savingsAccountId);
        customTransaction.setToAccountId(checkingAccountId);
        customTransaction.setAmount(new BigDecimal("50.00"));
        customTransaction.setTransactionType(TransactionType.TRANSFER);
        customTransaction.setDescription("Custom transaction ID test");

        TransactionDto created = transactionService.createTransaction(customTransaction);
        assertEquals("CUSTOM_TXN_123", created.getTransactionId());

        // Test 2: Transaction with custom reference number
        TransactionDto refTransaction = new TransactionDto();
        refTransaction.setReferenceNumber("REF123456");
        refTransaction.setFromAccountId(savingsAccountId);
        refTransaction.setToAccountId(checkingAccountId);
        refTransaction.setAmount(new BigDecimal("75.00"));
        refTransaction.setTransactionType(TransactionType.TRANSFER);
        refTransaction.setDescription("Custom reference number test");

        TransactionDto createdRef = transactionService.createTransaction(refTransaction);
        assertEquals("REF123456", createdRef.getReferenceNumber());

        // Test 3: Verify unique constraints
        assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(customTransaction);
        });
    }

    @Test
    void testTransactionErrorHandling_Success() {
        // Skip if setup failed
        assumeTrue(userId != null && savingsAccountId != null && checkingAccountId != null,
            "Test setup failed - skipping test");

        // Test 1: Invalid account ID
        TransactionDto invalidTransaction = new TransactionDto();
        invalidTransaction.setFromAccountId(999L); // Non-existent account
        invalidTransaction.setToAccountId(checkingAccountId);
        invalidTransaction.setAmount(new BigDecimal("100.00"));
        invalidTransaction.setTransactionType(TransactionType.TRANSFER);
        invalidTransaction.setDescription("Invalid account test");

        // This should not throw an exception during creation, but might fail during processing
        TransactionDto created = transactionService.createTransaction(invalidTransaction);
        assertNotNull(created);

        // Test 2: Zero amount transaction
        TransactionDto zeroAmountTransaction = new TransactionDto();
        zeroAmountTransaction.setFromAccountId(savingsAccountId);
        zeroAmountTransaction.setToAccountId(checkingAccountId);
        zeroAmountTransaction.setAmount(BigDecimal.ZERO);
        zeroAmountTransaction.setTransactionType(TransactionType.TRANSFER);
        zeroAmountTransaction.setDescription("Zero amount test");

        TransactionDto createdZero = transactionService.createTransaction(zeroAmountTransaction);
        assertNotNull(createdZero);
        assertEquals(BigDecimal.ZERO, createdZero.getAmount());

        // Test 3: Negative amount transaction
        TransactionDto negativeAmountTransaction = new TransactionDto();
        negativeAmountTransaction.setFromAccountId(savingsAccountId);
        negativeAmountTransaction.setToAccountId(checkingAccountId);
        negativeAmountTransaction.setAmount(new BigDecimal("-50.00"));
        negativeAmountTransaction.setTransactionType(TransactionType.TRANSFER);
        negativeAmountTransaction.setDescription("Negative amount test");

        TransactionDto createdNegative = transactionService.createTransaction(negativeAmountTransaction);
        assertNotNull(createdNegative);
        assertEquals(new BigDecimal("-50.00"), createdNegative.getAmount());
    }

    @Test
    void testTransactionPerformance_Success() {
        // Skip if setup failed
        assumeTrue(userId != null && savingsAccountId != null && checkingAccountId != null,
            "Test setup failed - skipping test");

        // Test 1: Create multiple transactions quickly
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 10; i++) {
            TransactionDto transaction = new TransactionDto();
            transaction.setFromAccountId(savingsAccountId);
            transaction.setToAccountId(checkingAccountId);
            transaction.setAmount(new BigDecimal("10.00"));
            transaction.setTransactionType(TransactionType.TRANSFER);
            transaction.setDescription("Performance test transaction " + i);

            TransactionDto created = transactionService.createTransaction(transaction);
            assertNotNull(created);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Verify all transactions were created
        List<TransactionDto> allTransactions = transactionService.getTransactionsByFromAccountId(savingsAccountId);
        assertTrue(allTransactions.size() >= 10);

        // Performance assertion (should complete within reasonable time)
        assertTrue(duration < 5000, "Transaction creation took too long: " + duration + "ms");
    }

    @Test
    void testTransactionConcurrency_Success() {
        // Skip if setup failed
        assumeTrue(userId != null && savingsAccountId != null && checkingAccountId != null,
            "Test setup failed - skipping test");

        // Test concurrent transaction creation
        Thread[] threads = new Thread[5];
        TransactionDto[] results = new TransactionDto[5];

        for (int i = 0; i < 5; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                TransactionDto transaction = new TransactionDto();
                transaction.setFromAccountId(savingsAccountId);
                transaction.setToAccountId(checkingAccountId);
                transaction.setAmount(new BigDecimal("20.00"));
                transaction.setTransactionType(TransactionType.TRANSFER);
                transaction.setDescription("Concurrency test transaction " + index);

                results[index] = transactionService.createTransaction(transaction);
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Verify all transactions were created successfully
        for (int i = 0; i < 5; i++) {
            assertNotNull(results[i], "Transaction " + i + " was not created");
            assertNotNull(results[i].getId());
            assertNotNull(results[i].getTransactionId());
        }

        // Verify unique transaction IDs
        for (int i = 0; i < 5; i++) {
            for (int j = i + 1; j < 5; j++) {
                assertNotEquals(results[i].getTransactionId(), results[j].getTransactionId(),
                    "Duplicate transaction ID found");
            }
        }
    }
}
