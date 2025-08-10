package com.banking.accountservice.service.impl;

import com.banking.accountservice.dto.AccountDto;
import com.banking.accountservice.entity.Account;
import com.banking.accountservice.entity.AccountStatus;
import com.banking.accountservice.entity.AccountType;
import com.banking.accountservice.entity.Currency;
import com.banking.accountservice.repository.AccountRepository;
import com.banking.accountservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        // Check if account number already exists
        if (existsByAccountNumber(accountDto.getAccountNumber())) {
            throw new RuntimeException("Account with number " + accountDto.getAccountNumber() + " already exists");
        }
        
        // Generate account number if not provided
        if (accountDto.getAccountNumber() == null || accountDto.getAccountNumber().isEmpty()) {
            accountDto.setAccountNumber(generateAccountNumber());
        }
        
        Account account = new Account(
            accountDto.getUserId(),
            accountDto.getAccountNumber(),
            accountDto.getAccountType(),
            accountDto.getCurrency()
        );
        
        Account savedAccount = accountRepository.save(account);
        return convertToDto(savedAccount);
    }
    
    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountById(Long id) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
        return convertToDto(account);
    }
    
    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new RuntimeException("Account not found with number: " + accountNumber));
        return convertToDto(account);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getAccountsByUserIdAndStatus(Long userId, AccountStatus status) {
        return accountRepository.findByUserIdAndStatus(userId, status).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getAccountsByType(AccountType accountType) {
        return accountRepository.findByAccountType(accountType).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getAccountsByCurrency(Currency currency) {
        return accountRepository.findByCurrency(currency).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getAccountsByStatus(AccountStatus status) {
        return accountRepository.findByStatus(status).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getAccountsByBalanceRange(BigDecimal minBalance, BigDecimal maxBalance) {
        return accountRepository.findByBalanceBetween(minBalance, maxBalance).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public AccountDto updateAccountStatus(Long id, AccountStatus status) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
        
        account.setStatus(status);
        Account updatedAccount = accountRepository.save(account);
        return convertToDto(updatedAccount);
    }
    
    @Override
    public AccountDto updateAccountBalance(Long id, BigDecimal newBalance) {
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Balance cannot be negative");
        }
        
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
        
        account.setBalance(newBalance);
        Account updatedAccount = accountRepository.save(account);
        return convertToDto(updatedAccount);
    }
    
    @Override
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new RuntimeException("Account not found with id: " + id);
        }
        accountRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByAccountNumber(String accountNumber) {
        return accountRepository.existsByAccountNumber(accountNumber);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countAccountsByUserIdAndStatus(Long userId, AccountStatus status) {
        return accountRepository.countByUserIdAndStatus(userId, status);
    }
    
    @Override
    public String generateAccountNumber() {
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder();
        
        // Generate 12-digit account number
        for (int i = 0; i < 12; i++) {
            accountNumber.append(random.nextInt(10));
        }
        
        String generatedNumber = accountNumber.toString();
        
        // Ensure uniqueness
        while (existsByAccountNumber(generatedNumber)) {
            accountNumber = new StringBuilder();
            for (int i = 0; i < 12; i++) {
                accountNumber.append(random.nextInt(10));
            }
            generatedNumber = accountNumber.toString();
        }
        
        return generatedNumber;
    }
    
    private AccountDto convertToDto(Account account) {
        AccountDto dto = new AccountDto();
        dto.setId(account.getId());
        dto.setUserId(account.getUserId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountType(account.getAccountType());
        dto.setBalance(account.getBalance());
        dto.setCurrency(account.getCurrency());
        dto.setStatus(account.getStatus());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setUpdatedAt(account.getUpdatedAt());
        return dto;
    }
}
