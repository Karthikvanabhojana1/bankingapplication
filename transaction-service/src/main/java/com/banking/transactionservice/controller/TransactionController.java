package com.banking.transactionservice.controller;

import com.banking.common.dto.ApiResponse;
import com.banking.transactionservice.dto.TransactionDto;
import com.banking.transactionservice.entity.TransactionStatus;
import com.banking.transactionservice.entity.TransactionType;
import com.banking.transactionservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<TransactionDto>> createTransaction(@Valid @RequestBody TransactionDto transactionDto) {
        try {
            TransactionDto createdTransaction = transactionService.createTransaction(transactionDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transaction created successfully", createdTransaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionDto>> getTransactionById(@PathVariable Long id) {
        try {
            TransactionDto transaction = transactionService.getTransactionById(id);
            return ResponseEntity.ok(ApiResponse.success("Transaction retrieved successfully", transaction));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/transaction-id/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionDto>> getTransactionByTransactionId(@PathVariable String transactionId) {
        try {
            TransactionDto transaction = transactionService.getTransactionByTransactionId(transactionId);
            return ResponseEntity.ok(ApiResponse.success("Transaction retrieved successfully", transaction));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/from-account/{fromAccountId}")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getTransactionsByFromAccountId(@PathVariable Long fromAccountId) {
        try {
            List<TransactionDto> transactions = transactionService.getTransactionsByFromAccountId(fromAccountId);
            return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/to-account/{toAccountId}")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getTransactionsByToAccountId(@PathVariable Long toAccountId) {
        try {
            List<TransactionDto> transactions = transactionService.getTransactionsByToAccountId(toAccountId);
            return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/account/{accountId}")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getTransactionsByAccountId(@PathVariable Long accountId) {
        try {
            List<TransactionDto> transactions = transactionService.getTransactionsByAccountId(accountId);
            return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/type/{transactionType}")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getTransactionsByType(@PathVariable TransactionType transactionType) {
        try {
            List<TransactionDto> transactions = transactionService.getTransactionsByType(transactionType);
            return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getTransactionsByStatus(@PathVariable TransactionStatus status) {
        try {
            List<TransactionDto> transactions = transactionService.getTransactionsByStatus(status);
            return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/amount/range")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getTransactionsByAmountRange(
            @RequestParam BigDecimal minAmount, @RequestParam BigDecimal maxAmount) {
        try {
            List<TransactionDto> transactions = transactionService.getTransactionsByAmountRange(minAmount, maxAmount);
            return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/date/range")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<TransactionDto> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/reference/{referenceNumber}")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getTransactionsByReferenceNumber(@PathVariable String referenceNumber) {
        try {
            List<TransactionDto> transactions = transactionService.getTransactionsByReferenceNumber(referenceNumber);
            return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TransactionDto>> updateTransactionStatus(
            @PathVariable Long id, @RequestParam TransactionStatus status) {
        try {
            TransactionDto updatedTransaction = transactionService.updateTransactionStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Transaction status updated successfully", updatedTransaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PatchMapping("/transaction-id/{transactionId}/status")
    public ResponseEntity<ApiResponse<TransactionDto>> updateTransactionStatusByTransactionId(
            @PathVariable String transactionId, @RequestParam TransactionStatus status) {
        try {
            TransactionDto updatedTransaction = transactionService.updateTransactionStatusByTransactionId(transactionId, status);
            return ResponseEntity.ok(ApiResponse.success("Transaction status updated successfully", updatedTransaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.ok(ApiResponse.success("Transaction deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/check/{transactionId}")
    public ResponseEntity<ApiResponse<Boolean>> checkTransactionExists(@PathVariable String transactionId) {
        try {
            boolean exists = transactionService.existsByTransactionId(transactionId);
            return ResponseEntity.ok(ApiResponse.success("Transaction check completed", exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/count/from-account/{accountId}/status/{status}")
    public ResponseEntity<ApiResponse<Long>> countTransactionsByFromAccountIdAndStatus(
            @PathVariable Long accountId, @PathVariable TransactionStatus status) {
        try {
            long count = transactionService.countTransactionsByFromAccountIdAndStatus(accountId, status);
            return ResponseEntity.ok(ApiResponse.success("Transaction count retrieved successfully", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/count/to-account/{accountId}/status/{status}")
    public ResponseEntity<ApiResponse<Long>> countTransactionsByToAccountIdAndStatus(
            @PathVariable Long accountId, @PathVariable TransactionStatus status) {
        try {
            long count = transactionService.countTransactionsByToAccountIdAndStatus(accountId, status);
            return ResponseEntity.ok(ApiResponse.success("Transaction count retrieved successfully", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/generate/transaction-id")
    public ResponseEntity<ApiResponse<String>> generateTransactionId() {
        try {
            String transactionId = transactionService.generateTransactionId();
            return ResponseEntity.ok(ApiResponse.success("Transaction ID generated successfully", transactionId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/generate/reference-number")
    public ResponseEntity<ApiResponse<String>> generateReferenceNumber() {
        try {
            String referenceNumber = transactionService.generateReferenceNumber();
            return ResponseEntity.ok(ApiResponse.success("Reference number generated successfully", referenceNumber));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}
