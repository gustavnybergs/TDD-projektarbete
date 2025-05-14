package com.bank.util;

/**
 * Konstanter för banksystemet.
 * Alla konfigurerbara värden samlas här för enkel underhåll.
 */
public class BankConstants {
    // Befintliga konstanter
    public static final String CARD_NUMBER_PATTERN = "\\d{12}";
    public static final int MAX_FAILED_ATTEMPTS = 3;

    // Nya konstanter för konfiguration

    // Autentisering
    public static final int MAX_AUTHENTICATION_ATTEMPTS = 3;

    // Sedelvalörer
    public static final int[] VALID_DENOMINATIONS = {100, 200, 500};

    // Valuta
    public static final String CURRENCY_SYMBOL = "kr";
    public static final String CURRENCY_DECIMAL_SEPARATOR = ",";
    public static final String CURRENCY_THOUSANDS_SEPARATOR = " ";

    // UI meddelanden
    public static final String CONFIRM_YES = "Y";
    public static final String CONFIRM_NO = "N";

    // Error meddelanden (kan flyttas hit later)
    public static final String ERROR_INVALID_AMOUNT = "Belopp måste vara större än noll";
    public static final String ERROR_INSUFFICIENT_FUNDS = "Otillräckligt saldo";
    public static final String ERROR_ACCOUNT_NOT_FOUND = "Kontot hittades inte";
}