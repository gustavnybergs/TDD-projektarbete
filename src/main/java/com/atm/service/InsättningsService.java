package com.atm.service;

import com.atm.integration.Transaktionslogg;
import com.atm.interfaces.Sedelräknare;
import com.atm.model.Konto;

import java.util.Map;

public class InsättningsService {
    private final Sedelräknare räknare;
    private final Transaktionslogg logg;

    public InsättningsService(Sedelräknare räknare, Transaktionslogg logg) {
        this.räknare = räknare;
        this.logg = logg;
    }

    public void sättIn(Konto konto, Map<Integer, Integer> sedlar, boolean bekräftat) {
        int belopp = räknare.räknaOchVerifiera(sedlar);

        if (!bekräftat) {
            System.out.println("Insättning avbruten – ej bekräftad.");
            return;
        }

        konto.sättIn(belopp);
        logg.loggaInsättning(konto.getKontonummer(), belopp);

        System.out.println("Insättning av " + belopp + " kr klar.");
        System.out.println("Vill du ha kvitto? (Simuleras)");
    }
}