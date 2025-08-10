package com.banking.transactionservice.service;

import java.util.function.Supplier;

public interface RetryService {
    
    <T> T executeWithRetry(Supplier<T> operation, String operationName);
    
    <T> T executeWithRetry(Supplier<T> operation, String operationName, int maxAttempts);
    
    void executeWithRetry(Runnable operation, String operationName);
    
    void executeWithRetry(Runnable operation, String operationName, int maxAttempts);
}
