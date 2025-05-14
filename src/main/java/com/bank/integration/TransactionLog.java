package com.bank.integration;

// Klass som ansvarar för att logga insättningar
public class TransactionLog {

    // Metod som loggar en insättning till konsolen
    // kontonummer - det konto som fick insättningen
    // belopp - hur mycket som sattes in
    public void loggaInsättning(String kontonummer, int belopp) {
        // Skriver ut ett meddelande om insättningen
        System.out.println("Loggad insättning: " + belopp + " kr till konto " + kontonummer);
    }
}
