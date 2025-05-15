package com.bank.service.transaction;

import com.bank.service.validation.ErrorCode;

public class OperationResult {
    private final boolean success;
    private final String message;
    private final ErrorCode errorCode;

    private OperationResult(boolean success, String message, ErrorCode errorCode) {
        this.success = success;
        this.message = message;
        this.errorCode = errorCode;
    }

    public static OperationResult success() {
        return new OperationResult(true, "Operation successful", null);
    }

    public static OperationResult success(String message) {
        return new OperationResult(true, message, null);
    }

    public static OperationResult failure(String message, ErrorCode errorCode) {
        return new OperationResult(false, message, errorCode);
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public ErrorCode getErrorCode() { return errorCode; }
}