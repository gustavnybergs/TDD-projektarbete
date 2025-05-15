package com.bank.service.validation;

public enum ErrorCode {
    // Account errors
    ACCOUNT_NOT_FOUND,
    ACCOUNT_EXISTS,

    // Transaction errors
    INVALID_AMOUNT,
    INSUFFICIENT_FUNDS,
    DEPOSIT_FAILED,
    WITHDRAWAL_FAILED,

    // Authentication errors
    INVALID_CARD,
    WRONG_PIN,
    CARD_BLOCKED,

    // System errors
    REPOSITORY_ERROR,
    VALIDATION_ERROR
}