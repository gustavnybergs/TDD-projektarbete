package com.bank.ui.handlers;

import com.bank.model.Account;
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