package com.banking.accountservice.integration;

import com.banking.accountservice.dto.AccountDto;
import com.banking.accountservice.entity.AccountStatus;
import com.banking.accountservice.entity.AccountType;
import com.banking.accountservice.entity.Currency;
import com.banking.accountservice.service.AccountService;
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
class AccountServiceIntegrationTest {

    @Autowired
    private AccountService accountService;

    private AccountDto savingsAccount;
    private AccountDto checkingAccount;
    private AccountDto businessAccount;

    @BeforeEach
    void setUp() {
        // Setup test data
        savingsAccount = new AccountDto();
        savingsAccount.setUserId(1L);
        savingsAccount.setAccountType(AccountType.SAVINGS);
        savingsAccount.setBalance(new BigDecimal("1000.00"));
        savingsAccount.setCurrency(Currency.USD);

        checkingAccount = new AccountDto();
        checkingAccount.setUserId(1L);
        checkingAccount.setAccountType(AccountType.CHECKING);
        checkingAccount.setBalance(new BigDecimal("500.00"));
        checkingAccount.setCurrency(Currency.USD);

        businessAccount = new AccountDto();
        businessAccount.setUserId(2L);
        businessAccount.setAccountType(AccountType.BUSINESS);
        businessAccount.setBalance(new BigDecimal("5000.00"));
        businessAccount.setCurrency(Currency.EUR);
    }

    @Test
    void testCreateAccount_Success() {
        // Arrange
        String accountNumber = accountService.generateAccountNumber();
        savingsAccount.setAccountNumber(accountNumber);

        // Act
        AccountDto result = accountService.createAccount(savingsAccount);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(accountNumber, result.getAccountNumber());
        assertEquals(AccountType.SAVINGS, result.getAccountType());
        assertEquals(Currency.USD, result.getCurrency());
        assertEquals(AccountStatus.ACTIVE, result.getStatus());
        assertEquals(1L, result.getUserId());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void testCreateAccount_WithInitialBalance_Success() {
        // Arrange
        String accountNumber = accountService.generateAccountNumber();
        savingsAccount.setAccountNumber(accountNumber);
        savingsAccount.setBalance(new BigDecimal("2500.00"));

        // Act
        AccountDto result = accountService.createAccount(savingsAccount);

        // Assert
        assertEquals(new BigDecimal("2500.00"), result.getBalance());
    }

    @Test
    void testCreateMultipleAccountsForSameUser_Success() {
        // Arrange
        String savingsNumber = accountService.generateAccountNumber();
        String checkingNumber = accountService.generateAccountNumber();
        
        savingsAccount.setAccountNumber(savingsNumber);
        checkingAccount.setAccountNumber(checkingNumber);

        // Act
        AccountDto savingsResult = accountService.createAccount(savingsAccount);
        AccountDto checkingResult = accountService.createAccount(checkingAccount);

        // Assert
        assertNotNull(savingsResult);
        assertNotNull(checkingResult);
        assertEquals(1L, savingsResult.getUserId());
        assertEquals(1L, checkingResult.getUserId());
        assertNotEquals(savingsResult.getAccountNumber(), checkingResult.getAccountNumber());
    }

    @Test
    void testGetAccountById_Success() {
        // Arrange
        String accountNumber = accountService.generateAccountNumber();
        savingsAccount.setAccountNumber(accountNumber);
        AccountDto created = accountService.createAccount(savingsAccount);

        // Act
        AccountDto result = accountService.getAccountById(created.getId());

        // Assert
        assertNotNull(result);
        assertEquals(created.getId(), result.getId());
        assertEquals(created.getAccountNumber(), result.getAccountNumber());
    }

    @Test
    void testGetAccountById_NotFound_ThrowsException() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            accountService.getAccountById(999L);
        });
    }

    @Test
    void testGetAccountByNumber_Success() {
        // Arrange
        String accountNumber = accountService.generateAccountNumber();
        savingsAccount.setAccountNumber(accountNumber);
        AccountDto created = accountService.createAccount(savingsAccount);

        // Act
        AccountDto result = accountService.getAccountByNumber(created.getAccountNumber());

        // Assert
        assertNotNull(result);
        assertEquals(created.getAccountNumber(), result.getAccountNumber());
    }

    @Test
    void testGetAccountsByUserId_Success() {
        // Arrange
        String savingsNumber = accountService.generateAccountNumber();
        String checkingNumber = accountService.generateAccountNumber();
        
        savingsAccount.setAccountNumber(savingsNumber);
        checkingAccount.setAccountNumber(checkingNumber);
        
        accountService.createAccount(savingsAccount);
        accountService.createAccount(checkingAccount);

        // Act
        List<AccountDto> results = accountService.getAccountsByUserId(1L);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(a -> a.getUserId().equals(1L)));
    }

    @Test
    void testGetAccountsByType_Success() {
        // Arrange
        String savingsNumber = accountService.generateAccountNumber();
        String businessNumber = accountService.generateAccountNumber();
        
        savingsAccount.setAccountNumber(savingsNumber);
        businessAccount.setAccountNumber(businessNumber);
        
        accountService.createAccount(savingsAccount);
        accountService.createAccount(businessAccount);

        // Act
        List<AccountDto> results = accountService.getAccountsByType(AccountType.SAVINGS);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(AccountType.SAVINGS, results.get(0).getAccountType());
    }

    @Test
    void testGetAccountsByCurrency_Success() {
        // Arrange
        String savingsNumber = accountService.generateAccountNumber();
        String businessNumber = accountService.generateAccountNumber();
        
        savingsAccount.setAccountNumber(savingsNumber);
        businessAccount.setAccountNumber(businessNumber);
        
        accountService.createAccount(savingsAccount);
        accountService.createAccount(businessAccount);

        // Act
        List<AccountDto> usdResults = accountService.getAccountsByCurrency(Currency.USD);
        List<AccountDto> eurResults = accountService.getAccountsByCurrency(Currency.EUR);

        // Assert
        assertEquals(1, usdResults.size());
        assertEquals(1, eurResults.size());
        assertEquals(Currency.USD, usdResults.get(0).getCurrency());
        assertEquals(Currency.EUR, eurResults.get(0).getCurrency());
    }

    @Test
    void testUpdateAccountBalance_Success() {
        // Arrange
        String accountNumber = accountService.generateAccountNumber();
        savingsAccount.setAccountNumber(accountNumber);
        AccountDto created = accountService.createAccount(savingsAccount);

        // Act
        AccountDto result = accountService.updateAccountBalance(created.getId(), new BigDecimal("1500.00"));

        // Assert
        assertEquals(new BigDecimal("1500.00"), result.getBalance());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void testUpdateAccountStatus_Success() {
        // Arrange
        String accountNumber = accountService.generateAccountNumber();
        savingsAccount.setAccountNumber(accountNumber);
        AccountDto created = accountService.createAccount(savingsAccount);

        // Act
        AccountDto result = accountService.updateAccountStatus(created.getId(), AccountStatus.SUSPENDED);

        // Assert
        assertEquals(AccountStatus.SUSPENDED, result.getStatus());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void testUpdateAccountStatus_InvalidStatus_ThrowsException() {
        // Arrange
        String accountNumber = accountService.generateAccountNumber();
        savingsAccount.setAccountNumber(accountNumber);
        AccountDto created = accountService.createAccount(savingsAccount);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            accountService.updateAccountStatus(created.getId(), null);
        });
    }

    @Test
    void testGetAccountsByBalanceRange_Success() {
        // Arrange
        String savingsNumber = accountService.generateAccountNumber();
        String checkingNumber = accountService.generateAccountNumber();
        
        savingsAccount.setAccountNumber(savingsNumber);
        checkingAccount.setAccountNumber(checkingNumber);
        
        accountService.createAccount(savingsAccount);
        accountService.createAccount(checkingAccount);

        // Act
        List<AccountDto> results = accountService.getAccountsByBalanceRange(
            new BigDecimal("400.00"), new BigDecimal("1200.00"));

        // Assert
        assertNotNull(results);
        assertTrue(results.size() >= 1);
        assertTrue(results.stream().allMatch(a -> 
            a.getBalance().compareTo(new BigDecimal("400.00")) >= 0 && 
            a.getBalance().compareTo(new BigDecimal("1200.00")) <= 0));
    }

    @Test
    void testCheckAccountExists_Success() {
        // Arrange
        String accountNumber = accountService.generateAccountNumber();
        savingsAccount.setAccountNumber(accountNumber);
        accountService.createAccount(savingsAccount);

        // Act
        boolean exists = accountService.existsByAccountNumber(accountNumber);

        // Assert
        assertTrue(exists);
    }

    @Test
    void testCheckAccountExists_NonExistentAccount_ReturnsFalse() {
        // Act
        boolean exists = accountService.existsByAccountNumber("NONEXISTENT123");

        // Assert
        assertFalse(exists);
    }

    @Test
    void testCountAccountsByUserIdAndStatus_Success() {
        // Arrange
        String savingsNumber = accountService.generateAccountNumber();
        String checkingNumber = accountService.generateAccountNumber();
        
        savingsAccount.setAccountNumber(savingsNumber);
        checkingAccount.setAccountNumber(checkingNumber);
        
        accountService.createAccount(savingsAccount);
        accountService.createAccount(checkingAccount);

        // Act
        long count = accountService.countAccountsByUserIdAndStatus(1L, AccountStatus.ACTIVE);

        // Assert
        assertEquals(2, count);
    }

    @Test
    void testGenerateAccountNumber_Success() {
        // Act
        String accountNumber = accountService.generateAccountNumber();

        // Assert
        assertNotNull(accountNumber);
        assertTrue(accountNumber.length() >= 10);
        assertTrue(accountNumber.matches("\\d+"));
    }

    @Test
    void testGenerateUniqueAccountNumbers_Success() {
        // Act
        String accountNumber1 = accountService.generateAccountNumber();
        String accountNumber2 = accountService.generateAccountNumber();

        // Assert
        assertNotNull(accountNumber1);
        assertNotNull(accountNumber2);
        assertNotEquals(accountNumber1, accountNumber2);
    }

    @Test
    void testDeleteAccount_Success() {
        // Arrange
        String accountNumber = accountService.generateAccountNumber();
        savingsAccount.setAccountNumber(accountNumber);
        AccountDto created = accountService.createAccount(savingsAccount);

        // Act
        accountService.deleteAccount(created.getId());

        // Assert
        assertThrows(RuntimeException.class, () -> {
            accountService.getAccountById(created.getId());
        });
    }
}
