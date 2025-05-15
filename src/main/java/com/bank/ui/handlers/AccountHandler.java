package com.bank.ui.handlers;

import com.bank.model.Account;
import com.bank.service.account.AccountService;
import com.bank.ui.UserInterface;

import java.util.List;

/**
 * Hanterar kontooperationer i bankomatgränssnittet.
 * Denna klass ansvarar för att visa kontoinformation, hjälpa användaren att
 * välja mellan tillgängliga konton, och visa saldo för valda konton.
 *
 * Uppdaterad för att använda UserInterface istället av hårdkodad Scanner.
 */
public class AccountHandler {
    private final UserInterface ui;
    private final AccountService accountService;
    private String authenticatedCardNumber;

    /**
     * Skapar en ny AccountHandler med angivna beroenden.
     *
     * @param ui UserInterface för användarinteraktion
     * @param accountService Service för att hämta och hantera kontoinformation
     */
    public AccountHandler(UserInterface ui, AccountService accountService) {
        this.ui = ui;
        this.accountService = accountService;
    }

    /**
     * Sätter kortnumret för den autentiserade användaren.
     * Detta används för att filtrera vilka konton som är tillgängliga.
     *
     * @param cardNumber Kortnumret för den autentiserade användaren
     */
    public void setAuthenticatedCardNumber(String cardNumber) {
        this.authenticatedCardNumber = cardNumber;
    }

    /**
     * Visar saldot för ett konto som användaren väljer.
     * Om inget konto väljs eller finns tillgängligt, visas inget saldo.
     */
    public void showBalance() {
        Account account = selectAccount();
        if (account != null) {
            ui.showMessage("Saldo på konto " + account.getAccountNumber() + ": " +
                    account.getFormattedBalance());
        }
    }

    /**
     * Hjälper användaren att välja ett konto från tillgängliga konton.
     * Visar en lista med tillgängliga konton och låter användaren välja.
     *
     * @return Det valda kontot om ett giltigt val gjordes, annars null
     */
    public Account selectAccount() {
        List<Account> accounts = getAvailableAccounts();
        if (accounts.isEmpty()) {
            ui.showMessage("Inga konton tillgängliga.");
            return null;
        }

        ui.showMessage("Välj konto:");
        for (int i = 0; i < accounts.size(); i++) {
            ui.showMessage((i + 1) + ". Konto " + accounts.get(i).getAccountNumber() +
                    " (" + accounts.get(i).getAccountName() + ")");
        }

        String input = ui.getInput("Ditt val: ");

        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < accounts.size()) {
                return accounts.get(index);
            }
        } catch (NumberFormatException ignored) {}

        ui.showError("Ogiltigt val.");
        return null;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    /**
     * Hämtar de konton som är tillgängliga för den autentiserade användaren.
     *
     * @return En lista med konton kopplade till användarens kort
     */
    private List<Account> getAvailableAccounts() {
        if (authenticatedCardNumber == null) {
            return List.of();
        }
        return accountService.getAccountRepository().findByCardNumber(authenticatedCardNumber);
    }
}