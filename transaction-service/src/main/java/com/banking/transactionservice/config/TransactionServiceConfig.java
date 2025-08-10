package com.banking.transactionservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "transaction.service")
public class TransactionServiceConfig {
    
    private int maxRetryAttempts = 3;
    private long retryDelayMs = 1000;
    private boolean enableAuditLogging = true;
    private String defaultCurrency = "USD";
    private int transactionIdLength = 20;
    private int referenceNumberLength = 10;
    
    // Getters and Setters
    public int getMaxRetryAttempts() {
        return maxRetryAttempts;
    }
    
    public void setMaxRetryAttempts(int maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
    }
    
    public long getRetryDelayMs() {
        return retryDelayMs;
    }
    
    public void setRetryDelayMs(long retryDelayMs) {
        this.retryDelayMs = retryDelayMs;
    }
    
    public boolean isEnableAuditLogging() {
        return enableAuditLogging;
    }
    
    public void setEnableAuditLogging(boolean enableAuditLogging) {
        this.enableAuditLogging = enableAuditLogging;
    }
    
    public String getDefaultCurrency() {
        return defaultCurrency;
    }
    
    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }
    
    public int getTransactionIdLength() {
        return transactionIdLength;
    }
    
    public void setTransactionIdLength(int transactionIdLength) {
        this.transactionIdLength = transactionIdLength;
    }
    
    public int getReferenceNumberLength() {
        return referenceNumberLength;
    }
    
    public void setReferenceNumberLength(int referenceNumberLength) {
        this.referenceNumberLength = referenceNumberLength;
    }
}
