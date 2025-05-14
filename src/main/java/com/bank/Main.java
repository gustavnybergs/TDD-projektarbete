package com.bank;

import com.bank.ui.ConsoleMenu;

public class Main {
    public static void main(String[] args) {
        System.out.println("Startar bankomatsystemet...");

        // Skapa och starta konsolgränssnittet
        ConsoleMenu menu = new ConsoleMenu();
        menu.start();
    }
}