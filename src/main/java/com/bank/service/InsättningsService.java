package com.bank.service;

import com.bank.integration.Transaktionslogg;
import com.bank.integration.Sedelräknare;
import com.bank.model.Account;

import java.util.Map;

public class InsättningsService {
    private final Sedelräknare räknare;
    private final Transaktionslogg logg;
    private final AccountService accountService;  // För att hantera Account-operationer

    public InsättningsService(Sedelräknare räknare, Transaktionslogg logg, AccountService accountService) {
        this.räknare = räknare;
        this.logg = logg;
        this.accountService = accountService;
    }

    public void sättIn(String accountNumber, Map<Integer, Integer> sedlar, boolean bekräftat) {
        // Hämta kontot via AccountService
        Account account = accountService.getAccount(accountNumber);
        if (account == null) {
            System.out.println("Kontot hittades inte");
            return;
        }

        int belopp = räknare.räknaOchVerifiera(sedlar);

        if (!bekräftat) {
            System.out.println("Insättning avbruten – ej bekräftad.");
            return;
        }

        // Uppdatera kontot via AccountService
        accountService.updatedBalance(accountNumber, account.getBalance() + belopp);

        // Logga transaktionen
        logg.loggaInsättning(accountNumber, belopp);

        System.out.println("Insättning av " + belopp + " kr klar.");
        System.out.println("Vill du ha kvitto? (Simuleras)");
    }
}