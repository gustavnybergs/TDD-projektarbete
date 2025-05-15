package com.bank.service.validation;

import com.bank.model.Account;
import com.bank.service.transaction.OperationResult;
import com.bank.service.account.AccountService;

/**
 * Hanterar felmeddelanden, validering och loggning i bankomatsystemet.
 * Denna klass ansvarar för att validera transaktioner, generera användaranpassade
 * felmeddelanden och logga fel för felsökning.
 *
 * Uppdaterad för att använda nya OperationResult-klasser för bättre felhantering.
 */

public class ErrorHandler {
    private final AccountService accountService;

    /**
     * Skapar en ny ErrorHandler med angivet AccountService.
     *
     * @param accountService Service för kontoåtkomst och validering
     */
    public ErrorHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Validerar ett uttag genom att kontrollera konto och belopp.
     * Uppdaterad för att använda OperationResult från AccountService.
     *
     * @param accountNumber Kontonumret för uttaget
     * @param amount Beloppet som ska tas ut
     * @return Ett ValidationResult som indikerar om uttaget är giltigt
     */
    public ValidationResult validateWithdrawal(String accountNumber, double amount) {
        // Kontrollera att kontot existerar med OperationResult
        OperationResult accountExistsResult = accountService.accountExists(accountNumber);
        if (!accountExistsResult.isSuccess()) {
            return ValidationResult.failure("Account does not exist");
        }

        // Kontrollera att beloppet är positivt
        if (amount < 0) {
            return ValidationResult.failure("Please enter a valid amount");
        }

        // Kontrollera att det finns tillräckligt med saldo med OperationResult
        OperationResult balanceResult = accountService.hasEnoughBalance(accountNumber, amount);
        if (!balanceResult.isSuccess()) {
            Account account = accountService.getAccount(accountNumber);
            double balance = account.getBalance();
            return ValidationResult.failure("Insufficient balance. Available: " +
                    formatAmount(balance) + ", Requested: " +
                    formatAmount(amount));
        }

        // Alla kontroller passerade
        return ValidationResult.success();
    }

    /**
     * Genererar ett meddelande för nätverksfel.
     * @return Felmeddelande för nätverksfel
     */
    public String getNetworkErrorMessage() {
        return "Network issue detected. Please try again later or contact support.";
    }

    /**
     * Genererar ett meddelande för avbruten transaktion.
     * @return Meddelande för avbruten transaktion
     */
    public String getTransactionCancelledMessage() {
        return "Transaction cancelled. Your account has not been charged.";
    }

    /**
     * Loggar ett fel med felkod och meddelande.
     * @param errorCode Felkod för identifiering
     * @param errorMessage Detaljerat felmeddelande
     * @return true om loggningen lyckades
     */
    public boolean logError(String errorCode, String errorMessage) {
        // Här skulle vi normalt logga till en fil eller databas när error sparats
        // För MVP skull returnerar vi bara true
        System.out.println("ERROR [" + errorCode + "]: " + errorMessage);
        return true;
    }

    /**
     * Hjälpmetod för att formatera belopp.
     *
     * @param amount Beloppet som ska formateras
     * @return Formaterat belopp som string
     */
    private String formatAmount(double amount) {
        return String.format("%.2f", amount);
    }
}