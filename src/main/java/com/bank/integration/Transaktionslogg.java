package com.bank.integration;

public class Transaktionslogg {
    public void loggaInsättning(String kontonummer, int belopp) {
        System.out.println("Loggad insättning: " + belopp + " kr till konto " + kontonummer);
    }
}
