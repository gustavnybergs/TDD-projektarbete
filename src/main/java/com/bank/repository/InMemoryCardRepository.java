package com.bank.repository;

import com.bank.model.Card;

import java.util.HashMap;
import java.util.Map;

public class InMemoryCardRepository  implements CardRepository{
    private final Map<String, Card> cards = new HashMap<>();

    /**
     * Sparar ett kort i lagringssystemet.
     * @param card Kortet som ska sparas
     */
    @Override
    public void saveCard(Card card) {
        cards.put(card.getCardNumber(), card);
    }

    /**
     * Hämtar ett kort baserat på kortnummer.
     * @param cardNumber Kortnumret för det kort som ska hämtas
     * @return Kortet om det finns, annars null
     */
    @Override
    public Card findCardByCardNumber(String cardNumber) {
        return cards.get(cardNumber);
    }
}
