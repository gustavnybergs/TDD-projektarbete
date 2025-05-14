package com.bank.ui.handlers;

import com.bank.service.AuthenticationResult;
import com.bank.service.AuthenticationService;
import com.bank.ui.UserInterface;
import com.bank.util.BankConstants;

import java.util.Scanner;

/**
 * Hanterar autentiseringsprocessen i bankomatgränssnittet.
 * Denna klass ansvarar för att samla in kortnummer och PIN-kod från användaren,
 * verifiera dessa uppgifter via AuthenticationService, och hantera misslyckade
 * inloggningsförsök.
 */

public class AuthenticationHandler {
    private final Scanner scanner;
    private final AuthenticationService authService;
    private final UserInterface ui;
    private String authenticatedCardNumber;

    /**
     * Skapar en ny AuthenticationHandler med angivna beroenden.
     *
     * @param scanner Scanner för inläsning av användarindata
     * @param authService Service för verifiering av autentiseringsuppgifter
     * @param ui Användargränssnitt för visning av maskerade uppgifter
     */
    public AuthenticationHandler(Scanner scanner, AuthenticationService authService, UserInterface ui) {
        this.scanner = scanner;
        this.authService = authService;
        this.ui = ui;
    }

    /**
     * Genomför autentiseringsprocessen genom att be användaren om kortnummer och PIN-kod.
     * Hanterar flera inloggningsförsök upp till ett maximalt antal.
     *
     * @return true om autentiseringen lyckades, annars false
     */
    public boolean authenticate() {
        int attempts = 0;
        // Använd konstant istället för hardcoded värde
        final int MAX_ATTEMPTS = BankConstants.MAX_AUTHENTICATION_ATTEMPTS;

        while (attempts < MAX_ATTEMPTS) {
            System.out.print("Ange ditt kortnummer (12 siffror): ");
            String cardNumber = scanner.nextLine().trim();

            System.out.print("Ange din PIN-kod: ");
            String pin = scanner.nextLine().trim();

            AuthenticationResult result = authService.authenticate(cardNumber, pin);

            switch (result) {
                case SUCCESS:
                    System.out.println("Inloggning lyckades!");
                    authenticatedCardNumber = cardNumber;
                    return true;
                case INVALID_CARD:
                    System.out.println("Ogiltigt kortnummer. Försök igen.");
                    break;
                case WRONG_PIN:
                    attempts++;
                    System.out.println("Felaktig PIN-kod. Försök igen. " +
                            "Försök kvar: " + (MAX_ATTEMPTS - attempts));
                    break;
                case CARD_BLOCKED:
                    System.out.println("Kortet är blockerat. Kontakta kundtjänst.");
                    return false;
            }
        }

        System.out.println("För många felaktiga försök. Kortet är nu blockerat.");
        return false;
    }

    /**
     * Hämtar kortnumret för den autentiserade användaren.
     *
     * @return Kortnumret om autentiseringen har lyckats, annars null
     */
    public String getAuthenticatedCardNumber() {
        return authenticatedCardNumber;
    }

}