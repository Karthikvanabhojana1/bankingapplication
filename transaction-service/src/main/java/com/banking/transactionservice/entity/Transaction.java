package com.banking.transactionservice.entity;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Transaction ID is required")
    @Column(name = "transaction_id", unique = true)
    private String transactionId;
    
    @Column(name = "from_account_id")
    private Long fromAccountId;
    
    @Column(name = "to_account_id")
    private Long toAccountId;
    
    @NotNull(message = "Amount is required")
    @Column(precision = 19, scale = 2)
    private BigDecimal amount;
    
    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType transactionType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status")
    private TransactionStatus status;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "reference_number")
    private String referenceNumber;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = TransactionStatus.PENDING;
        }
        validateTransaction();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        validateTransaction();
    }
    
    private void validateTransaction() {
        // Validate that fromAccountId is present for transactions that require it
        if (transactionType != null && 
            (transactionType == TransactionType.TRANSFER || 
             transactionType == TransactionType.WITHDRAWAL || 
             transactionType == TransactionType.PAYMENT || 
             transactionType == TransactionType.REFUND || 
             transactionType == TransactionType.LOAN_REPAYMENT)) {
            if (fromAccountId == null) {
                throw new IllegalArgumentException("From account ID is required for " + transactionType + " transactions");
            }
        }
        
        // Validate that toAccountId is present for transactions that require it
        if (transactionType == TransactionType.DEPOSIT || 
            transactionType == TransactionType.TRANSFER || 
            transactionType == TransactionType.PAYMENT || 
            transactionType == TransactionType.REFUND || 
            transactionType == TransactionType.LOAN_DISBURSEMENT) {
            if (toAccountId == null) {
                throw new IllegalArgumentException("To account ID is required for " + transactionType + " transactions");
            }
        }
        
        // Validate amount based on transaction type
        if (amount != null && transactionType != null) {
            if (transactionType == TransactionType.FEE || transactionType == TransactionType.REFUND) {
                // Fees and refunds can be zero or negative
                if (amount.compareTo(BigDecimal.ZERO) < 0 && transactionType == TransactionType.FEE) {
                    throw new IllegalArgumentException("Fee amount cannot be negative");
                }
            } else {
                // Other transaction types must have positive amounts
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Amount must be greater than 0 for " + transactionType + " transactions");
                }
            }
        }
    }
    
    // Constructors
    public Transaction() {}
    
    public Transaction(String transactionId, Long fromAccountId, Long toAccountId, 
                     BigDecimal amount, TransactionType transactionType, String description) {
        this.transactionId = transactionId;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public Long getFromAccountId() {
        return fromAccountId;
    }
    
    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }
    
    public Long getToAccountId() {
        return toAccountId;
    }
    
    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public TransactionType getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
    
    public TransactionStatus getStatus() {
        return status;
    }
    
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", transactionId='" + transactionId + '\'' +
                ", fromAccountId=" + fromAccountId +
                ", toAccountId=" + toAccountId +
                ", amount=" + amount +
                ", transactionType=" + transactionType +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
