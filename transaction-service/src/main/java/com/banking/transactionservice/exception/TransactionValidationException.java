package com.banking.transactionservice.exception;

public class TransactionValidationException extends RuntimeException {
    
    public TransactionValidationException(String message) {
        super(message);
    }
    
    public TransactionValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
