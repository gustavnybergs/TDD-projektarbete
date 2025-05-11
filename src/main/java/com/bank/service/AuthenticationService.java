package com.bank.service;

import com.bank.model.Card;
import com.bank.repository.CardRepository;
import com.bank.util.BankConstants;
import com.bank.util.CardValidator;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationService implements Authenticator {

    // För att visa att de inte ska ändras efter initiering.
    // Förstärker immutability och gör koden säkrare.
    private final CardRepository cardRepository;
    private final Map<String, Boolean> authenticatedCards;
    private final CardValidator cardValidator;

    public AuthenticationService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
        authenticatedCards = new HashMap<>();
        cardValidator = new CardValidator();
    }

    /**
     * Validerar om ett kortnummer har rätt format.
     * @param cardNumber Kortnumret som ska valideras
     * @return true om kortnumret är giltigt, annars false
     */

    public boolean validateCardNumber(String cardNumber) {
        return cardValidator.isValidCardNumber(cardNumber);
    }

    /**
     * Registrerar ett kort i systemet.
     * @param card Kortet som ska registreras
     */
    public void registerCard(Card card) {
        cardRepository.saveCard(card);
    }

    /**
     * Autentiserar en användare med kortnummer och PIN-kod.
     * @param cardNumber Användarens kortnummer
     * @param pin Användarens PIN-kod
     * @return Resultatet av autentiseringen
     */

    @Override
    public AuthenticationResult authenticate(String cardNumber, String pin) {
        Card card = cardRepository.findCardByCardNumber(cardNumber);

        if (card == null) {
            return AuthenticationResult.INVALID_CARD;
        }

        if (card.isBlocked()) {
            return AuthenticationResult.CARD_BLOCKED;
        }

        boolean isPinCorrect = card.verifyPin(pin);

        if (isPinCorrect) {
            authenticatedCards.put(cardNumber, true);
            return AuthenticationResult.SUCCESS;
        } else {
            return AuthenticationResult.WRONG_PIN;
        }
    }

    /**
     * Kontrollerar om ett kort har tillgång till banktjänster.
     * @param cardNumber Kortnumret som ska kontrolleras
     * @return true om kortet har tillgång, annars false
     */
    public boolean hasAccessToBankServices(String cardNumber) {
        // Enkel implementation för att få testerna att passera
        return authenticatedCards.getOrDefault(cardNumber, false);
    }
}
