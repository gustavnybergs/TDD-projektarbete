package com.bank.repository;
// Interface för kortlagring

import com.bank.model.Card;

public interface CardRepository {
    void saveCard(Card card);
    Card findCardByCardNumber(String cardNumber);
}
