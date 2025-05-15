package com.bank.ui;

import java.util.Scanner;

/**
 * Console-implementation av UserInterface.
 * Hanterar användarinteraktion via kommandorad/terminal.
 * Uppdaterad från ursprunglig ConsoleUI för att implementera hela interfacet.
 */
public class ConsoleUI implements UserInterface {
    private final Scanner scanner;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void showError(String errorMessage) {
        System.err.println("FEL: " + errorMessage);
    }

    @Override
    public boolean confirmAction(String message) {
        String input = getInput(message + " (Y/N): ");
        return input.toUpperCase().equals("Y");
    }

    @Override
    public String maskSensitiveInput(String input) {
        if (input == null) {
            return null;
        }
        return "*".repeat(input.length());
    }

    // För framtida cleanup om behövs
    public void close() {
        scanner.close();
    }
}