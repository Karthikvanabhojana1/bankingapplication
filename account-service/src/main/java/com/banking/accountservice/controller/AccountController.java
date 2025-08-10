package com.banking.accountservice.controller;

import com.banking.common.dto.ApiResponse;
import com.banking.accountservice.dto.AccountDto;
import com.banking.accountservice.entity.AccountStatus;
import com.banking.accountservice.entity.AccountType;
import com.banking.accountservice.entity.Currency;
import com.banking.accountservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<AccountDto>> createAccount(@Valid @RequestBody AccountDto accountDto) {
        try {
            AccountDto createdAccount = accountService.createAccount(accountDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Account created successfully", createdAccount));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountDto>> getAccountById(@PathVariable Long id) {
        try {
            AccountDto account = accountService.getAccountById(id);
            return ResponseEntity.ok(ApiResponse.success("Account retrieved successfully", account));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountDto>> getAccountByNumber(@PathVariable String accountNumber) {
        try {
            AccountDto account = accountService.getAccountByNumber(accountNumber);
            return ResponseEntity.ok(ApiResponse.success("Account retrieved successfully", account));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AccountDto>>> getAccountsByUserId(@PathVariable Long userId) {
        try {
            List<AccountDto> accounts = accountService.getAccountsByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success("Accounts retrieved successfully", accounts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<ApiResponse<List<AccountDto>>> getAccountsByUserIdAndStatus(
            @PathVariable Long userId, @PathVariable AccountStatus status) {
        try {
            List<AccountDto> accounts = accountService.getAccountsByUserIdAndStatus(userId, status);
            return ResponseEntity.ok(ApiResponse.success("Accounts retrieved successfully", accounts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/type/{accountType}")
    public ResponseEntity<ApiResponse<List<AccountDto>>> getAccountsByType(@PathVariable AccountType accountType) {
        try {
            List<AccountDto> accounts = accountService.getAccountsByType(accountType);
            return ResponseEntity.ok(ApiResponse.success("Accounts retrieved successfully", accounts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/currency/{currency}")
    public ResponseEntity<ApiResponse<List<AccountDto>>> getAccountsByCurrency(@PathVariable Currency currency) {
        try {
            List<AccountDto> accounts = accountService.getAccountsByCurrency(currency);
            return ResponseEntity.ok(ApiResponse.success("Accounts retrieved successfully", accounts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<AccountDto>>> getAccountsByStatus(@PathVariable AccountStatus status) {
        try {
            List<AccountDto> accounts = accountService.getAccountsByStatus(status);
            return ResponseEntity.ok(ApiResponse.success("Accounts retrieved successfully", accounts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/balance/range")
    public ResponseEntity<ApiResponse<List<AccountDto>>> getAccountsByBalanceRange(
            @RequestParam BigDecimal minBalance, @RequestParam BigDecimal maxBalance) {
        try {
            List<AccountDto> accounts = accountService.getAccountsByBalanceRange(minBalance, maxBalance);
            return ResponseEntity.ok(ApiResponse.success("Accounts retrieved successfully", accounts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AccountDto>> updateAccountStatus(
            @PathVariable Long id, @RequestParam AccountStatus status) {
        try {
            AccountDto updatedAccount = accountService.updateAccountStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Account status updated successfully", updatedAccount));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/balance")
    public ResponseEntity<ApiResponse<AccountDto>> updateAccountBalance(
            @PathVariable Long id, @RequestParam BigDecimal newBalance) {
        try {
            AccountDto updatedAccount = accountService.updateAccountBalance(id, newBalance);
            return ResponseEntity.ok(ApiResponse.success("Account balance updated successfully", updatedAccount));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable Long id) {
        try {
            accountService.deleteAccount(id);
            return ResponseEntity.ok(ApiResponse.success("Account deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/check/{accountNumber}")
    public ResponseEntity<ApiResponse<Boolean>> checkAccountExists(@PathVariable String accountNumber) {
        try {
            boolean exists = accountService.existsByAccountNumber(accountNumber);
            return ResponseEntity.ok(ApiResponse.success("Account check completed", exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/count/user/{userId}/status/{status}")
    public ResponseEntity<ApiResponse<Long>> countAccountsByUserIdAndStatus(
            @PathVariable Long userId, @PathVariable AccountStatus status) {
        try {
            long count = accountService.countAccountsByUserIdAndStatus(userId, status);
            return ResponseEntity.ok(ApiResponse.success("Account count retrieved successfully", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/generate-number")
    public ResponseEntity<ApiResponse<String>> generateAccountNumber() {
        try {
            String accountNumber = accountService.generateAccountNumber();
            return ResponseEntity.ok(ApiResponse.success("Account number generated successfully", accountNumber));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}
