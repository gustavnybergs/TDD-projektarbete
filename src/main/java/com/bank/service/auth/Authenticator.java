package com.bank.service.auth;

public interface Authenticator {
    AuthenticationResult authenticate(String cardNumber, String pin);
    boolean hasAccessToBankServices(String cardNumber);
}
