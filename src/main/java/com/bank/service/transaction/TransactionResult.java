package com.bank.service.transaction;

import com.bank.service.validation.ErrorCode;

import java.util.Optional;

public class TransactionResult {
    private final boolean success;
    private final String message;
    private final ErrorCode errorCode;
    private final Optional<Double> newBalance;

    private TransactionResult(boolean success, String message, ErrorCode errorCode, Double newBalance) {
        this.success = success;
        this.message = message;
        this.errorCode = errorCode;
        this.newBalance = Optional.ofNullable(newBalance);
    }

    public static TransactionResult success() {
        return new TransactionResult(true, null, null, null);
    }

    public static TransactionResult success(double newBalance) {
        return new TransactionResult(true, "Operation successful", null, newBalance);
    }

    public static TransactionResult failure(String message, ErrorCode errorCode) {
        return new TransactionResult(false, message, errorCode, null);
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public ErrorCode getErrorCode() { return errorCode; }
    public Optional<Double> getNewBalance() { return newBalance; }
}