package com.bank.model;

import com.bank.util.BankConstants;
/*
* Representerar ett bankkort med information och funktionalitet.
* Ansvarar för verifiering av PIN-kod och hantering av felaktiga inloggningsförsök.
* */

public class Card implements PinVerifier{
    private String cardNumber;
    private String expiryDate;
    private String pin;
    private boolean blocked;
    private int failedAttempts;

    public Card(String cardNumber, String expiryDate, String pin) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.pin = pin;
        this.blocked = false;
        this.failedAttempts = 0;
    }

    /*
    * Verifierar om den angivna PIN-koden matchar kortets PIN.
    * Om felaktig PIN anges tre gånger blockeras kortet.
    * 
    * @param enteredPin PIN-koden som ska verifieras
    * @return true om PIN-koden är korrekt, annars false
    *
    * */

    @Override
    public boolean verifyPin(String enteredPin) {
        if (blocked) {
            return false;
        }

        boolean isCorrect = this.pin.equals(enteredPin);
        if (!isCorrect) {
            failedAttempts++; // vi implementerar blockering senare
            if (failedAttempts >= BankConstants.MAX_FAILED_ATTEMPTS) {
                blocked = true;
            }
        } else {
            failedAttempts = 0; // Återställ räknaren vid korrekt PIN
        }
         return isCorrect;
    }

    public String getCardNumber() {
        return cardNumber;
    }
    public String getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
    public String getPin() {
        return pin;
    }
    public boolean isBlocked() {
        return blocked;
    }
    public int getFailedAttempts() {
        return failedAttempts;
    }
}
