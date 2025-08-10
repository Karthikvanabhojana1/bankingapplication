package com.banking.paymentservice.controller;

import com.banking.common.dto.ApiResponse;
import com.banking.paymentservice.dto.PaymentDto;
import com.banking.paymentservice.entity.PaymentStatus;
import com.banking.paymentservice.entity.PaymentType;
import com.banking.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentDto>> createPayment(@Valid @RequestBody PaymentDto paymentDto) {
        try {
            PaymentDto created = paymentService.createPayment(paymentDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Payment created successfully", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentDto>> getPaymentById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Payment retrieved successfully", paymentService.getPaymentById(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/payment-id/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentDto>> getPaymentByPaymentId(@PathVariable String paymentId) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Payment retrieved successfully", paymentService.getPaymentByPaymentId(paymentId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/from-account/{fromAccountId}")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPaymentsByFromAccount(@PathVariable Long fromAccountId) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Payments retrieved successfully", paymentService.getPaymentsByFromAccount(fromAccountId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/to-account/{toAccountId}")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPaymentsByToAccount(@PathVariable Long toAccountId) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Payments retrieved successfully", paymentService.getPaymentsByToAccount(toAccountId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPaymentsByAccount(@PathVariable Long accountId) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Payments retrieved successfully", paymentService.getPaymentsByAccount(accountId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Payments retrieved successfully", paymentService.getPaymentsByStatus(status)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPaymentsByType(@PathVariable PaymentType type) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Payments retrieved successfully", paymentService.getPaymentsByType(type)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/amount/range")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPaymentsByAmountRange(@RequestParam BigDecimal minAmount, @RequestParam BigDecimal maxAmount) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Payments retrieved successfully", paymentService.getPaymentsByAmountRange(minAmount, maxAmount)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/date/range")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPaymentsByDateRange(@RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Payments retrieved successfully", paymentService.getPaymentsByDateRange(startDate, endDate)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/reference/{referenceNumber}")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPaymentsByReference(@PathVariable String referenceNumber) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Payments retrieved successfully", paymentService.getPaymentsByReferenceNumber(referenceNumber)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PaymentDto>> updatePaymentStatus(@PathVariable Long id, @RequestParam PaymentStatus status) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Payment status updated successfully", paymentService.updatePaymentStatus(id, status)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}


