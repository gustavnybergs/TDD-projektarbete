package com.atm.ui;

import com.atm.model.Konto;
import com.atm.service.InsättningsService;
import com.atm.integration.SimuleradSedelräknare;
import com.atm.integration.Transaktionslogg;

import java.util.*;

public class KonsolMeny {
    private final Scanner scanner = new Scanner(System.in);
    private final List<Konto> konton = new ArrayList<>();
    private final InsättningsService insättningsService;

    public KonsolMeny() {
        // Initiera testkonton
        konton.add(new Konto("1001", 500));
        konton.add(new Konto("1002", 1200));

        var räknare = new SimuleradSedelräknare();
        var logg = new Transaktionslogg();
        insättningsService = new InsättningsService(räknare, logg);
    }

    public void start() {
        while (true) {
            System.out.println("\n--- Bankomat Huvudmeny ---");
            System.out.println("1. Sätt in pengar");
            System.out.println("2. Visa saldo");
            System.out.println("0. Avsluta");
            System.out.print("Välj ett alternativ: ");
            String val = scanner.nextLine();

            switch (val) {
                case "1" -> sättInPengar();
                case "2" -> visaSaldo();
                case "0" -> {
                    System.out.println("Avslutar. Hej då!");
                    return;
                }
                default -> System.out.println("Ogiltigt val.");
            }
        }
    }

    private void visaSaldo() {
        Konto konto = väljKonto();
        if (konto != null) {
            System.out.println("Saldo på konto " + konto.getKontonummer() + ": " + konto.getSaldo() + " kr");
        }
    }

    private void sättInPengar() {
        Konto konto = väljKonto();
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
            insättningsService.sättIn(konto, sedlar, true);
            System.out.print("Vill du ha kvitto? (Y/N): ");
            String kvitto = scanner.nextLine().trim().toUpperCase();
            if (kvitto.equals("Y")) {
                System.out.println("Kvitto: Du satte in " + summa + " kr på konto " + konto.getKontonummer());
            }
        } else {
            System.out.println("Insättning avbruten.");
        }
    }

    private Konto väljKonto() {
        System.out.println("Välj konto:");
        for (int i = 0; i < konton.size(); i++) {
            System.out.println((i + 1) + ". Konto " + konton.get(i).getKontonummer());
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
}