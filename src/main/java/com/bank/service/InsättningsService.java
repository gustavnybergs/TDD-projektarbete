package com.bank.service;

import com.bank.integration.Transaktionslogg;
import com.bank.integration.Sedelräknare;
import com.bank.model.Account;

import java.util.Map;

// Tjänst för att hantera insättningar till ett konto
public class InsättningsService {

    // Använder Sedelräknare för att räkna summan av sedlar
    private final Sedelräknare räknare;

    // Använder Transaktionslogg för att logga insättningen
    private final Transaktionslogg logg;

    // Använder AccountService för att hämta och uppdatera konton
    private final AccountService accountService;

    // Konstruktor – tar emot de komponenter som behövs för insättning
    public InsättningsService(Sedelräknare räknare, Transaktionslogg logg, AccountService accountService) {
        this.räknare = räknare;
        this.logg = logg;
        this.accountService = accountService;
    }

    // Metod för att sätta in pengar på ett konto
    // accountNumber – kontot att sätta in pengar på
    // sedlar – en karta med sedelvalörer och antal (t.ex. {500=2, 100=3})
    // bekräftat – true om användaren bekräftat insättningen
    public void sättIn(String accountNumber, Map<Integer, Integer> sedlar, boolean bekräftat) {

        // Hämta kontot via AccountService
        Account account = accountService.getAccount(accountNumber);
        if (account == null) {
            System.out.println("Kontot hittades inte");
            return; // Avsluta om kontot inte finns
        }

        // Räkna ihop summan av sedlarna
        int belopp = räknare.räknaOchVerifiera(sedlar);

        // Kontrollera om insättningen är bekräftad av användaren
        if (!bekräftat) {
            System.out.println("Insättning avbruten – ej bekräftad.");
            return; // Avsluta om ej bekräftat
        }

        // Uppdatera kontots saldo med det nya beloppet
        accountService.updatedBalance(accountNumber, account.getBalance() + belopp);

        // Logga insättningen
        logg.loggaInsättning(accountNumber, belopp);

        // Skriv ut kvittoinformation (simulerat)
        System.out.println("Insättning av " + belopp + " kr klar.");
        System.out.println("Vill du ha kvitto? (Simuleras)");
    }
}