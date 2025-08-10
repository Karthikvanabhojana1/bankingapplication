package com.banking.paymentservice.entity;

public enum PaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED,
    DECLINED,
    REFUNDED,
    PARTIALLY_REFUNDED
}
