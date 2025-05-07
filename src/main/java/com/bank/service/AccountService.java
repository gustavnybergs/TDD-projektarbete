package com.bank.service;

import com.bank.model.Account;
import com.bank.repository.AccountRepository;

/**
 * Service-klass för hantering av konton i bankomaten.
 *
 * Denna klass fungerar som en mellanhand mellan kontroller och datalagring
 * och innehåller affärslogik för att hantera bankkonton såsom
 * kontoinformation, saldokontroll och saldouppdatering.
 */
public class AccountService {
    private final AccountRepository accountRepository;

    /**
     * Skapar en ny AccountService med det angivna repository.
     * @param accountRepository Repository för kontolagring och -åtkomst
     */
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Hämtar ett konto baserat på dess kontonummer.
     * @param accountNumber Kontonumret för kontot som ska hämtas
     * @return Konto-objektet om det finns, annars null
     */
    public Account getAccount(String accountNumber) {
        // Delegerar anrop till repository-lagret
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public AccountRepository getAccountRepository() {
        return accountRepository;
    }

    /**
     * Uppdaterar saldot på ett befintligt konto.
     * Eftersom Account-klassen är immutable skapas ett nytt konto med
     * samma kontonummer och namn men med det nya saldot.
     * @param accountNumber Kontonumret för kontot som ska uppdateras
     * @param newBalance Det nya saldot för kontot
     * @return Det uppdaterade kontot om det lyckas, annars null
     */
    public Account updatedBalance(String accountNumber, double newBalance) {
        // Hämta befintligt konto
        Account account = getAccount(accountNumber);
        if (account == null) {
            return null; // returnerar null när existerande konto som ska uppdateras inte finns.
        }

        /** Skapa ett nytt konto med uppdaterat saldo (eftersom Account är immutable)
        * Detta är inte samma sak som att skapa ett nytt bankkonto från användarens perspektiv.
         * Från systemets perspektiv ersätter vi representationen av samma konto
         * med en ny version som har uppdaterat saldo.
        */
        Account updatedAccount = new Account(
            account.getAccountNumber(), account.getAccountName(), newBalance
        );

        // ersätter befintligt accountobjekt med updatedAccount med nytt saldo
        accountRepository.saveAccount(updatedAccount);
        return updatedAccount;
    }

    /**
     * Kontrollerar om ett konto med angivet kontonummer existerar.
     * @param accountNumber Kontonumret som ska kontrolleras
     * @return true om kontot existerar, annars false
     */
    public boolean accountExists(String accountNumber) {
        return getAccount(accountNumber) != null;
    }

    /**
     * Verifierar om ett konto har tillräckligt med pengar för ett begärt belopp.
     *
     * @param accountNumber Kontonumret som ska kontrolleras
     * @param amount Beloppet som ska verifieras mot saldot
     * @return true om kontot existerar och har tillräckligt med pengar, annars false
     */
    public boolean hasEnoughBalance (String accountNumber, double amount) {
        // Hämtar konto
        Account account = getAccount(accountNumber);

        // Kontrollera om kontot existerar och har tillräckligt med saldo
        return account != null && account.getBalance() >= amount;
    }

    /**
     * Hämtar det formaterade saldot för ett konto.
     *
     * @param accountNumber Kontonumret för kontot
     * @return Formaterat saldo som en sträng om kontot finns, annars null
     */
    public String getFormattedBalance (String accountNumber) {
        // Hämta kontot
        Account account = getAccount(accountNumber);

        // Returnera formaterat saldo om kontot finns
        return account !=null ? account.getFormattedBalance() : null; // ternär operation
    }
}
