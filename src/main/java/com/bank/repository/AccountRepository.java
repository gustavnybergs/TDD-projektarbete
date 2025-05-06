package com.bank.repository;

import com.bank.model.Account;
import java.util.List;

/**
 * Interface för hantering av konton i systemet.
 */


public interface AccountRepository {
    /**
     * Sparar ett konto i repositoryt.
     * @param account Kontot som ska sparas
     */
    void saveAccount(Account account);

    /**
     * Hämtar ett konto baserat på kontonummer.
     * @param accountNumber Kontonumret för kontot som ska hämtas
     * @return Kontot om det finns, annars null
     */
    Account findByAccountNumber(String accountNumber);

    /**
     * Kopplar ett konto till ett kort.
     * @param accountNumber Kontonumret för kontot
     * @param cardNumber Kortnumret för kortet
     */
    void linkAccountToCard(String accountNumber, String cardNumber);

    /**
     * Hämtar alla konton kopplade till ett specifikt kort.
     * @param cardNumber Kortnumret för kortet
     * @return Lista med konton kopplade till kortet
     */
    List<Account> findByCardNumber(String cardNumber);


}
