package com.bank.ui.handlers;

import com.bank.model.Account;
import com.bank.service.AccountService;
import com.bank.service.InsättningsService;
import com.bank.integration.SimuleradSedelräknare;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Hanterar transaktioner i bankomatgränssnittet.
 * Denna klass ansvarar för att hantera insättningar och andra transaktionsrelaterade
 * operationer, såsom att samla in information om sedlar och bekräfta transaktioner.
 */

public class TransactionHandler {
    private final Scanner scanner;
    private final InsättningsService insättningsService;
    private final AccountHandler accountHandler;

    /**
     * Skapar en ny TransactionHandler med angivna beroenden.
     *
     * @param scanner Scanner för inläsning av användarindata
     * @param insättningsService Service för att hantera insättningar
     * @param accountHandler Handler för att välja konton
     */
    public TransactionHandler(Scanner scanner, InsättningsService insättningsService,
                              AccountHandler accountHandler) {
        this.scanner = scanner;
        this.insättningsService = insättningsService;
        this.accountHandler = accountHandler;
    }

    /**
     * Hanterar processen för att ta ut pengar från ett konto.
     * Denna metod guidar användaren genom hela uttagsprocessen:
     * - Välja konto
     * - Ange belopp
     * - Bekräfta uttaget
     * - Generera kvitto (om önskat)
     *
     * Metoden inkluderar validering och felhantering för användarindata.
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

        // Försök genomföra uttaget
        try {
            AccountService accountService = accountHandler.getAccountService();

            // Bekräfta uttaget med användaren
            System.out.print("Bekräfta uttag av " + amount + " kr? (Y/N): ");
            String confirm = scanner.nextLine().trim().toUpperCase();

            if (confirm.equals("Y")) {
                boolean success = accountService.withdraw(account.getAccountNumber(), amount);

                if (success) {
                    System.out.println("Uttag genomfört. Ta dina pengar.");

                    // Erbjud kvitto
                    System.out.print("Vill du ha kvitto? (Y/N): ");
                    String receipt = scanner.nextLine().trim().toUpperCase();
                    if (receipt.equals("Y")) {
                        System.out.println("Kvitto: Du tog ut " + amount + " kr från konto " +
                                account.getAccountNumber());
                        Account updatedAccount = accountService.getAccount(account.getAccountNumber());
                        System.out.println("Nytt saldo: " + updatedAccount.getFormattedBalance());
                    }
                } else {
                    System.out.println("Uttaget misslyckades. Kontakta kundtjänst.");
                }
            } else {
                System.out.println("Uttag avbrutet.");
            }
        } catch (IllegalArgumentException e) {
            // Hantera fel som kan uppstå vid uttag
            System.out.println("Fel: " + e.getMessage());
        }
    }

    /**
     * Hanterar en insättningsprocess.
     * Låter användaren välja konto, ange antal sedlar, och bekräfta insättningen.
     * Om användaren önskar, skrivs ett kvitto ut efter slutförd insättning.
     */
    public void handleDeposit() {
        Account konto = accountHandler.selectAccount();
        if (konto == null) return;

        Map<Integer, Integer> sedlar = new HashMap<>();
        System.out.println("Ange antal sedlar för varje valör (0 om inga):");
        int[] valörer = {100, 200, 500};
        for (int valör : valörer) {
            System.out.print(valör + " kr: ");
            int antal = Integer.parseInt(scanner.nextLine());
            if (antal > 0) {
                sedlar.put(valör, antal);
            }
        }

        int summa = new SimuleradSedelräknare().räknaOchVerifiera(sedlar);
        System.out.println("Totalt att sätta in: " + summa + " kr");
        System.out.print("Bekräfta insättning? (Y/N): ");
        String bekräfta = scanner.nextLine().trim().toUpperCase();

        if (bekräfta.equals("Y")) {
            insättningsService.sättIn(konto.getAccountNumber(), sedlar, true);
            System.out.print("Vill du ha kvitto? (Y/N): ");
            String kvitto = scanner.nextLine().trim().toUpperCase();
            if (kvitto.equals("Y")) {
                System.out.println("Kvitto: Du satte in " + summa + " kr på konto " +
                        konto.getAccountNumber());
            }
        } else {
            System.out.println("Insättning avbruten.");
        }
    }
}