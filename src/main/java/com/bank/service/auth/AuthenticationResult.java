package com.bank.service.auth;
// En enum för autentiseringsresultat

public enum AuthenticationResult {
    SUCCESS("Autentisering lyckades"),
    INVALID_CARD("Ogiltigt kort"),
    WRONG_PIN("Felaktig PIN-kod"),
    CARD_BLOCKED("Kortet är blockerat");

    private final String message;

    AuthenticationResult(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
