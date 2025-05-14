package com.bank.integration;

// Klass som ansvarar för att logga insättningar
public class TransactionLog {

    // Metod som loggar en insättning till konsolen
    // accountNumber - det konto som fick insättningen
    // amount - hur mycket som sattes in
    public void logDeposit(String accountNumber, int amount) {
        // Skriver ut ett meddelande om insättningen
        System.out.println("Loggad insättning: " + amount + " kr till konto " + accountNumber);
    }
}