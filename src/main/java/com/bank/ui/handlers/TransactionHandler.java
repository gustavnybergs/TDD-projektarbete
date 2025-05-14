package com.bank.ui.handlers;

import com.bank.model.Account;
import com.bank.service.AccountService;
import com.bank.service.TransactionResult;
import com.bank.integration.SimulatedNoteCounter;
import com.bank.util.BankConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Hanterar transaktioner i bankomatgränssnittet.
 * Denna klass ansvarar för att hantera insättningar och uttag med bättre felhantering
 * genom användning av TransactionResult och OperationResult.
 *
 * Uppdaterad för att använda AccountService för både uttag och insättningar.
 */

public class TransactionHandler {
    private final Scanner scanner;
    private final AccountHandler accountHandler;

    /**
     * Skapar en ny TransactionHandler med angivna beroenden.
     *
     * @param scanner Scanner för inläsning av användarindata
     * @param accountHandler Handler för att välja konton och komma åt AccountService
     */
    public TransactionHandler(Scanner scanner, AccountHandler accountHandler) {
        this.scanner = scanner;
        this.accountHandler = accountHandler;
    }

    /**
     * Hanterar processen för att ta ut pengar från ett konto.
     * Uppdaterad för att använda TransactionResult för bättre felhantering.
     */
    public void handleWithdrawal() {
        // Låt användaren välja konto
        Account account = accountHandler.selectAccount();
        if (account == null) return;

        // Visa aktuellt saldo
        System.out.println("Aktuellt saldo: " + account.getFormattedBalance());

        // Låt användaren ange belopp
        System.out.print("Ange belopp att ta ut: ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt belopp. Försök igen.");
            return;
        }

        AccountService accountService = accountHandler.getAccountService();

        // Bekräfta uttaget med användaren - använd konstant
        System.out.print("Bekräfta uttag av " + amount + " kr? (" + BankConstants.CONFIRM_YES + "/" + BankConstants.CONFIRM_NO + "): ");
        String confirm = scanner.nextLine().trim().toUpperCase();

        if (confirm.equals(BankConstants.CONFIRM_YES)) {
            // Använd den nya withdraw-metoden med TransactionResult
            TransactionResult result = accountService.withdraw(account.getAccountNumber(), amount);

            if (result.isSuccess()) {
                System.out.println("Uttag genomfört. Ta dina pengar.");

                // Erbjud kvitto - använd konstant
                System.out.print("Vill du ha kvitto? (" + BankConstants.CONFIRM_YES + "/" + BankConstants.CONFIRM_NO + "): ");
                String receipt = scanner.nextLine().trim().toUpperCase();
                if (receipt.equals(BankConstants.CONFIRM_YES)) {
                    System.out.println("Kvitto: Du tog ut " + amount + " kr från konto " +
                            account.getAccountNumber());
                    // Visa nytt saldo från TransactionResult
                    if (result.getNewBalance().isPresent()) {
                        System.out.println("Nytt saldo: " + String.format("%.2f", result.getNewBalance().get()) + " " + BankConstants.CURRENCY_SYMBOL);
                    }
                }
            } else {
                // Visa detaljerat felmeddelande från TransactionResult
                System.out.println("Uttaget misslyckades: " + result.getMessage());
            }
        } else {
            System.out.println("Uttag avbrutet.");
        }
    }

    /**
     * Hanterar en insättningsprocess.
     * Uppdaterad för att använda AccountService istället för DepositService.
     */
    public void handleDeposit() {
        Account account = accountHandler.selectAccount();
        if (account == null) return;

        Map<Integer, Integer> notes = new HashMap<>();
        System.out.println("Ange antal sedlar för varje valör (0 om inga):");

        // Använd konstant istället för hardcoded array
        for (int denomination : BankConstants.VALID_DENOMINATIONS) {
            System.out.print(denomination + " " + BankConstants.CURRENCY_SYMBOL + ": ");
            try {
                int count = Integer.parseInt(scanner.nextLine());
                if (count > 0) {
                    notes.put(denomination, count);
                }
            } catch (NumberFormatException e) {
                System.out.println("Ogiltigt antal för " + denomination + " " + BankConstants.CURRENCY_SYMBOL + " sedlar.");
                return;
            }
        }

        try {
            int total = new SimulatedNoteCounter().countAndVerify(notes);
            System.out.println("Totalt att sätta in: " + total + " " + BankConstants.CURRENCY_SYMBOL);

            // Använd konstanter för bekräftelse
            System.out.print("Bekräfta insättning? (" + BankConstants.CONFIRM_YES + "/" + BankConstants.CONFIRM_NO + "): ");
            String confirm = scanner.nextLine().trim().toUpperCase();

            // Använd AccountService för insättning nu!
            AccountService accountService = accountHandler.getAccountService();
            TransactionResult result = accountService.deposit(account.getAccountNumber(), notes, confirm.equals(BankConstants.CONFIRM_YES));

            if (result.isSuccess()) {
                System.out.print("Vill du ha kvitto? (" + BankConstants.CONFIRM_YES + "/" + BankConstants.CONFIRM_NO + "): ");
                String receipt = scanner.nextLine().trim().toUpperCase();
                if (receipt.equals(BankConstants.CONFIRM_YES)) {
                    System.out.println("Kvitto: Du satte in " + total + " " + BankConstants.CURRENCY_SYMBOL + " på konto " +
                            account.getAccountNumber());
                    // Visa nytt saldo från TransactionResult
                    if (result.getNewBalance().isPresent()) {
                        System.out.println("Nytt saldo: " + String.format("%.2f", result.getNewBalance().get()) + " " + BankConstants.CURRENCY_SYMBOL);
                    }
                }
            } else {
                // Visa detaljerat felmeddelande från TransactionResult
                System.out.println("Insättningen misslyckades: " + result.getMessage());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Fel: " + e.getMessage());
        }
    }
}