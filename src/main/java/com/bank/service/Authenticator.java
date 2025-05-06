package com.bank.service;

public interface Authenticator {
    AuthenticationResult authenticate(String cardNumber, String pin);
    boolean hasAccessToBankServices(String cardNumber);
}
