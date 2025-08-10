package com.banking.accountservice.service;

import com.banking.accountservice.dto.AccountDto;
import com.banking.accountservice.entity.AccountStatus;
import com.banking.accountservice.entity.AccountType;
import com.banking.accountservice.entity.Currency;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    
    AccountDto createAccount(AccountDto accountDto);
    
    AccountDto getAccountById(Long id);
    
    AccountDto getAccountByNumber(String accountNumber);
    
    List<AccountDto> getAccountsByUserId(Long userId);
    
    List<AccountDto> getAccountsByUserIdAndStatus(Long userId, AccountStatus status);
    
    List<AccountDto> getAccountsByType(AccountType accountType);
    
    List<AccountDto> getAccountsByCurrency(Currency currency);
    
    List<AccountDto> getAccountsByStatus(AccountStatus status);
    
    List<AccountDto> getAccountsByBalanceRange(BigDecimal minBalance, BigDecimal maxBalance);
    
    AccountDto updateAccountStatus(Long id, AccountStatus status);
    
    AccountDto updateAccountBalance(Long id, BigDecimal newBalance);
    
    void deleteAccount(Long id);
    
    boolean existsByAccountNumber(String accountNumber);
    
    long countAccountsByUserIdAndStatus(Long userId, AccountStatus status);
    
    String generateAccountNumber();
}
