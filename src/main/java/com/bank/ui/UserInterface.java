package com.bank.ui;

/**
 * Interface som definierar de metoder som krävs för användarinteraktion.
 * Detta möjliggör olika UI-implementationer (console, web, mobile)
 * utan att ändra business logic.
 *
 * Utökat från ursprungliga interface för att inkludera alla UI-operationer.
 */
public interface UserInterface {

    /**
     * Hämtar input från användaren med en given prompt.
     * @param prompt Meddelandet som visas för användaren
     * @return Användarens input som sträng
     */
    String getInput(String prompt);

    /**
     * Visar ett meddelande för användaren.
     * @param message Meddelandet som ska visas
     */
    void showMessage(String message);

    /**
     * Visar ett felmeddelande för användaren.
     * @param errorMessage Felmeddelandet som ska visas
     */
    void showError(String errorMessage);

    /**
     * Ber användaren bekräfta en handling.
     * @param message Meddelandet för bekräftelse
     * @return true om användaren bekräftar, false annars
     */
    boolean confirmAction(String message);

    /**
     * Maskerar känslig input (t.ex. PIN-kod).
     * @param input Känsliga data som ska maskeras
     * @return Maskerad sträng för visning
     */
    String maskSensitiveInput(String input);
}