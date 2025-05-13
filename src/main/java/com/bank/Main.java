package com.bank;

import com.bank.ui.ConsoleMeny;

public class Main {
    public static void main(String[] args) {
        System.out.println("Startar bankomatsystemet...");

        // Skapa och starta konsolgränssnittet
        ConsoleMeny meny = new ConsoleMeny();
        meny.start();
    }
}