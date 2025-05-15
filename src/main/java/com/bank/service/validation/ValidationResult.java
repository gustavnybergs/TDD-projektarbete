package com.bank.service.validation;

/**
 * Representerar resultatet av en validering.
 * Innehåller information om valideringen lyckades och eventuellt felmeddelande.
 */

public class ValidationResult {
    private final boolean valid;
    private final String errorMessage;

    private ValidationResult(boolean valid, String errorMessage) {
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    /**
     * Skapar ett lyckat valideringsresultat utan felmeddelande.
     *
     * @return Ett valideringsresultat som indikerar framgång
     */
    public static ValidationResult success() {
        return new ValidationResult(true, null);
    }

    /**
     * Skapar ett misslyckat valideringsresultat med ett felmeddelande.
     *
     * @param errorMessage Felmeddelande som beskriver valideringsfelet
     * @return Ett valideringsresultat som indikerar misslyckande med tillhörande felmeddelande
     */
    public static ValidationResult failure(String errorMessage) {
        return new ValidationResult(false, errorMessage);
    }

    /**
     * Kontrollerar om valideringen lyckades.
     *
     * @return true om valideringen lyckades, annars false
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Hämtar felmeddelandet om valideringen misslyckades.
     *
     * @return Felmeddelande eller null om valideringen lyckades
     */
    public String getErrorMessage() {
        return errorMessage;
    }

}
