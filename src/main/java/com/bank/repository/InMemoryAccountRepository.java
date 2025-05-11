package com.bank.repository;

import com.bank.model.Account;

import java.util.*;

/**
 * En minnesbaserad implementation av AccountRepository-interfacet.
 * Denna klass lagrar konton och deras relationer till kort i minnet för
 * utvecklings- och testningssyfte.
 *
 * Relationsmodell:
 * - Ett kort kan ha tillgång till flera konton (t.ex. lönekonto, sparkonto)
 * - Ett konto kan potentiellt nås av flera kort (t.ex. gemensamt konto)
 */

public class InMemoryAccountRepository implements AccountRepository {

    // accounts Lagrar konton med kontonummer som nyckel
    // cardToAccountLinks Lagrar kopplingar mellan kort och konton (kortnummer -> lista av kontonummer)
    // accountToCardLinks Håller reda på vilka kort som redan är kopplade till ett visst konto
    private final Map<String, Account> accounts= new HashMap<>();
    private final Map<String, List<String>> cardToAccountLinks = new HashMap<>();
    private final Map<String, List<String>> accountToCardLinks = new HashMap<>();

    /**
     * Sparar ett konto i minnet.
     * Om ett konto med samma kontonummer redan finns ersätts det.
     * @param account Kontot som ska sparas
     */
    @Override
    public void saveAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    /**
     * Hämtar ett konto baserat på kontonumret.
     * @param accountNumber Kontonumret för kontot som ska hämtas
     * @return Kontot om det finns, annars null
     */
    @Override
    public Account findByAccountNumber(String accountNumber) {
        return accounts.get(accountNumber);
    }

    /**
     * Kopplar ett konto till ett kort.
     *
     * I bankomatsystem är det vanligt att:
     * - Ett kort kan ha tillgång till flera konton (huvudfallet)
     * - Ett konto kan i vissa fall nås av flera kort (t.ex. gemensamma konton)
     *
     * Denna metod lägger till kopplingen i båda riktningarna för att
     * möjliggöra senare kontroll och eventuella begränsningar.
     *
     * @param accountNumber Kontonumret för kontot
     * @param cardNumber Kortnumret för kortet
     */
    @Override
    public void linkAccountToCard(String accountNumber, String cardNumber) {
        // Kontrollera att kontot finns
        if (!accounts.containsKey(accountNumber)) {
            throw new IllegalArgumentException("Account number " + accountNumber + " does not exist");
        }

        // Lägger till koppling från kort till konto
        cardToAccountLinks.computeIfAbsent(cardNumber, k -> new ArrayList<>()).add(accountNumber);

        // Lägger även till koppling från konto till kort (för spårbarhet)
        accountToCardLinks.computeIfAbsent(accountNumber, k -> new ArrayList<>()).add(cardNumber);


    }

    /**
     * Hämtar alla konton kopplade till ett specifikt kort.
     *
     * Detta är en central funktion i bankomatsystem där användaren
     * efter inloggning behöver se alla tillgängliga konton.
     *
     * @param cardNumber Kortnumret för kortet
     * @return Lista med konton kopplade till kortet, tom lista om inga konton hittas
     */
    @Override
    public List<Account> findByCardNumber(String cardNumber) {
        // Hämta lista med kontonummer för det angivna kortet
        List<String> accountNumbers = cardToAccountLinks.getOrDefault(cardNumber, Collections.emptyList());
        List<Account> result = new ArrayList<>();

        // Hämta varje konto från accounts-kartan och lägg till i resultatlistan
        for (String accountNumber : accountNumbers) {
            Account account = findByAccountNumber(accountNumber);
            if (account != null) {
                result.add(account);
            }
        }
        return result;
    }

    /**
     * Kontrollerar om ett konto redan är kopplat till ett specifikt kort.
     * Kan användas för att undvika duplicerade kopplingar.
     *
     * @param accountNumber Kontonumret
     * @param cardNumber Kortnumret
     * @return true om kopplingen redan finns, annars false
     */
    public boolean isAccountLinkedToCard(String accountNumber, String cardNumber) {
        List<String> linkedCards = accountToCardLinks.getOrDefault(cardNumber, Collections.emptyList());
        return linkedCards.contains(cardNumber);
    }

    /**
     * Returnerar antalet kort som är kopplade till ett specifikt konto.
     * Kan användas för att implementera begränsningar på antal kort per konto.
     *
     * @param accountNumber Kontonumret
     * @return Antalet kort kopplade till kontot
     */
    public int getNumberOfLinkedCards(String accountNumber) {
        return accountToCardLinks.getOrDefault(accountNumber, Collections.emptyList()).size();
    }
}
/*
* men är denna klass EncryptedAccountRepository nästa steg i vårt bygge? jag undrade bara hur vi skulle göra med den
*
* */