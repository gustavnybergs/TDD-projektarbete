package com.bank;

import com.bank.ui.KonsolMeny;

public class Main {
    public static void main(String[] args) {
        System.out.println("Startar bankomatsystemet...");

        // Skapa och starta konsolgränssnittet
        KonsolMeny meny = new KonsolMeny();
        meny.start();
    }
}