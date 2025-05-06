package com.bank.ui;

public class ConsoleUI implements UserInterface{
    @Override
    public String maskSensitiveInput(String input) {
        if (input == null) {
            return null;
        }
        return "*".repeat(input.length());
    }
    // Kommande metoder för konsolgränssnittet
}
