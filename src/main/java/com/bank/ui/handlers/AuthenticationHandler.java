package com.bank.ui.handlers;

import com.bank.service.auth.AuthenticationResult;
import com.bank.service.auth.AuthenticationService;
import com.bank.ui.UserInterface;
import com.bank.util.BankConstants;

/**
 * Hanterar autentiseringsprocessen i bankomatgränssnittet.
 * Denna klass ansvarar för att samla in kortnummer och PIN-kod från användaren,
 * verifiera dessa uppgifter via AuthenticationService, och hantera misslyckade
 * inloggningsförsök.
 *
 * Uppdaterad för att använda UserInterface istället av hårdkodad Scanner.
 */
public class AuthenticationHandler {
    private final UserInterface ui;
    private final AuthenticationService authService;
    private String authenticatedCardNumber;

    /**
     * Skapar en ny AuthenticationHandler med angivna beroenden.
     *
     * @param ui UserInterface för användarinteraktion
     * @param authService Service för verifiering av autentiseringsuppgifter
     */
    public AuthenticationHandler(UserInterface ui, AuthenticationService authService) {
        this.ui = ui;
        this.authService = authService;
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
            String cardNumber = ui.getInput("Ange ditt kortnummer (12 siffror): ");
            String pin = ui.getInput("Ange din PIN-kod: ");

            AuthenticationResult result = authService.authenticate(cardNumber, pin);

            switch (result) {
                case SUCCESS:
                    ui.showMessage("Inloggning lyckades!");
                    authenticatedCardNumber = cardNumber;
                    return true;
                case INVALID_CARD:
                    attempts++;
                    ui.showError("Ogiltigt kortnummer. Försök igen.");
                    break;
                case WRONG_PIN:
                    attempts++;
                    ui.showError("Felaktig PIN-kod. Försök igen. " +
                            "Försök kvar: " + (MAX_ATTEMPTS - attempts));
                    break;
                case CARD_BLOCKED:
                    ui.showError("Kortet är blockerat. Kontakta kundtjänst.");
                    return false;
            }
        }

        ui.showError("För många felaktiga försök. Kortet är nu blockerat.");
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