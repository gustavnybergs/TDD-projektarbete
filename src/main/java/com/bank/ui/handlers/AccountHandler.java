package com.bank.ui.handlers;

import com.bank.model.Account;
import com.bank.service.AccountService;

import java.util.List;
import java.util.Scanner;

/**
 * Hanterar kontooperationer i bankomatgränssnittet.
 * Denna klass ansvarar för att visa kontoinformation, hjälpa användaren att
 * välja mellan tillgängliga konton, och visa saldo för valda konton.
 */

public class AccountHandler {
    private final Scanner scanner;
    private final AccountService accountService;
    private String authenticatedCardNumber;

    /**
     * Skapar en ny AccountHandler med angivna beroenden.
     *
     * @param scanner Scanner för inläsning av användarindata
     * @param accountService Service för att hämta och hantera kontoinformation
     */
    public AccountHandler(Scanner scanner, AccountService accountService) {
        this.scanner = scanner;
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
            System.out.println("Saldo på konto " + account.getAccountNumber() + ": " +
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
        List<Account> konton = getAvailableAccounts();
        if (konton.isEmpty()) {
            System.out.println("Inga konton tillgängliga.");
            return null;
        }

        System.out.println("Välj konto:");
        for (int i = 0; i < konton.size(); i++) {
            System.out.println((i + 1) + ". Konto " + konton.get(i).getAccountNumber() +
                    " (" + konton.get(i).getAccountName() + ")");
        }

        System.out.print("Ditt val: ");
        String input = scanner.nextLine();

        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < konton.size()) {
                return konton.get(index);
            }
        } catch (NumberFormatException ignored) {}

        System.out.println("Ogiltigt val.");
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