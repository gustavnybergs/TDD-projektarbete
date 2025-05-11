package com.bank.util;
// Klass för validering

public class CardValidator {
    public boolean isValidCardNumber(String cardNumber) {
        return cardNumber != null && cardNumber.matches(BankConstants.CARD_NUMBER_PATTERN);
    }
}
