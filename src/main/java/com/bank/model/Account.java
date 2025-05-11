package com.bank.model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Representerar ett bankkonto med nummer, namn och saldo.
 * Ansvarar för att hålla kontots information och formatera saldo.
 */
public class Account {
    private final String accountNumber;
    private final String accountName;
    private final double balance;

/**
    * accountNumber och accountName till final-fält eftersom dessa värden inte
    * bör ändras efter att kontot har skapats, vilket stärker inkapslingen.
    * */

    /**
     * Skapar ett nytt konto med angivet nummer, namn och saldo.
     *
     * @param accountNumber Kontots unika nummer
     * @param accountName Kontots namn/beskrivning
     * @param balance Kontots startbalans
     * @throws IllegalArgumentException om någon parameter är ogiltig
     */


    public Account(String accountNumber, String accountName, double balance) {
        // Validera kontonummer
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }

        // Validera kontonamn
        if (accountName == null || accountName.trim().isEmpty()) {
            throw new IllegalArgumentException("Account name cannot be empty");
        }

        // Validera saldo
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }

        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.balance = balance;
    }

    /**
     * Hämtar kontots nummer.
     * @return kontots nummer
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Hämtar kontots namn.
     * @return kontots namn
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * Hämtar kontots saldo.
     * @return kontots saldo
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Hämtar kontots saldo formaterat med valutasymbol.
     * @return formaterat saldo med valutasymbol
     */
    public String getFormattedBalance() {
        // Explicit formatering utan att förlita sig på lokala formatmallar
        String formatted = String.format("%.2f", balance)
                .replace(".", ",");

        // Lägg till tusentalsavgränsare manuellt
        if (balance >= 1000) {
            formatted = formatted.substring(0, formatted.length() - 6) + " " + formatted.substring(formatted.length() - 6);
        }

        return formatted + " kr";
    }
}
