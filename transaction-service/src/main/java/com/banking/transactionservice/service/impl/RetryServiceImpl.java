package com.banking.transactionservice.service.impl;

import com.banking.transactionservice.service.RetryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class RetryServiceImpl implements RetryService {
    
    private static final Logger logger = LoggerFactory.getLogger(RetryServiceImpl.class);
    
    @Autowired
    private com.banking.transactionservice.config.TransactionServiceConfig config;
    
    @Override
    public <T> T executeWithRetry(Supplier<T> operation, String operationName) {
        return executeWithRetry(operation, operationName, config.getMaxRetryAttempts());
    }
    
    @Override
    public <T> T executeWithRetry(Supplier<T> operation, String operationName, int maxAttempts) {
        int attempt = 1;
        Exception lastException = null;
        
        while (attempt <= maxAttempts) {
            try {
                logger.debug("Executing {} - Attempt {}/{}", operationName, attempt, maxAttempts);
                return operation.get();
            } catch (Exception e) {
                lastException = e;
                logger.warn("Operation {} failed on attempt {}/{}: {}", operationName, attempt, maxAttempts, e.getMessage());
                
                if (attempt < maxAttempts) {
                    try {
                        Thread.sleep(config.getRetryDelayMs());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry operation interrupted", ie);
                    }
                }
                attempt++;
            }
        }
        
        throw new RuntimeException("Operation " + operationName + " failed after " + maxAttempts + " attempts", lastException);
    }
    
    @Override
    public void executeWithRetry(Runnable operation, String operationName) {
        executeWithRetry(operation, operationName, config.getMaxRetryAttempts());
    }
    
    @Override
    public void executeWithRetry(Runnable operation, String operationName, int maxAttempts) {
        executeWithRetry(() -> {
            operation.run();
            return null;
        }, operationName, maxAttempts);
    }
}
