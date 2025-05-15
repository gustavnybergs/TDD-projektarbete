package com.bank.ui.handlers;

import com.bank.model.Account;
import com.bank.service.account.AccountService;
import com.bank.service.transaction.TransactionResult;
import com.bank.integration.SimulatedNoteCounter;
import com.bank.util.BankConstants;
import com.bank.ui.UserInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * Hanterar transaktioner i bankomatgränssnittet.
 * Denna klass ansvarar för att hantera insättningar och uttag med bättre felhantering
 * genom användning av TransactionResult och OperationResult.
 *
 * Uppdaterad för att använda AccountService för både uttag och insättningar.
 * Uppdaterad för att använda UserInterface istället av hårdkodad Scanner.
 */
public class TransactionHandler {
    private final UserInterface ui;
    private final AccountHandler accountHandler;

    /**
     * Skapar en ny TransactionHandler med angivna beroenden.
     *
     * @param ui UserInterface för användarinteraktion
     * @param accountHandler Handler för att välja konton och komma åt AccountService
     */
    public TransactionHandler(UserInterface ui, AccountHandler accountHandler) {
        this.ui = ui;
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
        ui.showMessage("Aktuellt saldo: " + account.getFormattedBalance());

        // Låt användaren ange belopp
        String amountStr = ui.getInput("Ange belopp att ta ut: ");
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            ui.showError("Ogiltigt belopp. Försök igen.");
            return;
        }

        AccountService accountService = accountHandler.getAccountService();

        // Bekräfta uttaget med användaren - använd konstant
        boolean confirmed = ui.confirmAction("Bekräfta uttag av " + amount + " kr?");

        if (confirmed) {
            // Använd den nya withdraw-metoden med TransactionResult
            TransactionResult result = accountService.withdraw(account.getAccountNumber(), amount);

            if (result.isSuccess()) {
                ui.showMessage("Uttag genomfört. Ta dina pengar.");

                // Erbjud kvitto - använd konstant
                boolean wantReceipt = ui.confirmAction("Vill du ha kvitto?");
                if (wantReceipt) {
                    ui.showMessage("Kvitto: Du tog ut " + amount + " kr från konto " +
                            account.getAccountNumber());
                    // Visa nytt saldo från TransactionResult
                    if (result.getNewBalance().isPresent()) {
                        ui.showMessage("Nytt saldo: " + String.format("%.2f", result.getNewBalance().get()) + " " + BankConstants.CURRENCY_SYMBOL);
                    }
                }
            } else {
                // Visa detaljerat felmeddelande från TransactionResult
                ui.showError("Uttaget misslyckades: " + result.getMessage());
            }
        } else {
            ui.showMessage("Uttag avbrutet.");
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
        ui.showMessage("Ange antal sedlar för varje valör (0 om inga):");

        // Använd konstant istället för hardcoded array
        for (int denomination : BankConstants.VALID_DENOMINATIONS) {
            String input = ui.getInput(denomination + " " + BankConstants.CURRENCY_SYMBOL + ": ");
            try {
                int count = Integer.parseInt(input);
                if (count > 0) {
                    notes.put(denomination, count);
                }
            } catch (NumberFormatException e) {
                ui.showError("Ogiltigt antal för " + denomination + " " + BankConstants.CURRENCY_SYMBOL + " sedlar.");
                return;
            }
        }

        try {
            int total = new SimulatedNoteCounter().countAndVerify(notes);
            ui.showMessage("Totalt att sätta in: " + total + " " + BankConstants.CURRENCY_SYMBOL);

            // Använd konstanter för bekräftelse
            boolean confirmed = ui.confirmAction("Bekräfta insättning?");

            // Använd AccountService för insättning nu!
            AccountService accountService = accountHandler.getAccountService();
            TransactionResult result = accountService.deposit(account.getAccountNumber(), notes, confirmed);

            if (result.isSuccess()) {
                boolean wantReceipt = ui.confirmAction("Vill du ha kvitto?");
                if (wantReceipt) {
                    ui.showMessage("Kvitto: Du satte in " + total + " " + BankConstants.CURRENCY_SYMBOL + " på konto " +
                            account.getAccountNumber());
                    // Visa nytt saldo från TransactionResult
                    if (result.getNewBalance().isPresent()) {
                        ui.showMessage("Nytt saldo: " + String.format("%.2f", result.getNewBalance().get()) + " " + BankConstants.CURRENCY_SYMBOL);
                    }
                }
            } else {
                // Visa detaljerat felmeddelande från TransactionResult
                ui.showError("Insättningen misslyckades: " + result.getMessage());
            }
        } catch (IllegalArgumentException e) {
            ui.showError("Fel: " + e.getMessage());
        }
    }
}