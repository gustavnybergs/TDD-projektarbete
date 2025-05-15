package com.bank.service.account;

import com.bank.model.Account;
import com.bank.repository.AccountRepository;
import com.bank.service.validation.ErrorCode;
import com.bank.service.transaction.OperationResult;
import com.bank.service.transaction.TransactionResult;

import java.util.Map;

/**
 * Service-klass för hantering av konton i bankomaten.
 *
 * Denna klass fungerar som en mellanhand mellan kontroller och datalagring
 * och innehåller affärslogik för att hantera bankkonton såsom
 * kontoinformation, saldokontroll och saldouppdatering.
 *
 * Uppdaterad för att hantera både uttag och insättningar.
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
     * Returnerar detaljerad information om resultatet.
     *
     * @param accountNumber Kontonumret som ska kontrolleras
     * @return OperationResult med information om kontot existerar eller inte
     */
    public OperationResult accountExists(String accountNumber) {
        Account account = getAccount(accountNumber);
        if (account != null) {
            return OperationResult.success("Kontot existerar");
        } else {
            return OperationResult.failure("Kontot hittades inte", ErrorCode.ACCOUNT_NOT_FOUND);
        }
    }

    /**
     * Verifierar om ett konto har tillräckligt med pengar för ett begärt belopp.
     * Returnerar detaljerad information om saldokontroll och eventuella fel.
     *
     * @param accountNumber Kontonumret som ska kontrolleras
     * @param amount Beloppet som ska verifieras mot saldot
     * @return OperationResult med detaljerad information om saldokontroll
     */
    public OperationResult hasEnoughBalance(String accountNumber, double amount) {
        // Hämtar konto
        Account account = getAccount(accountNumber);

        if (account == null) {
            return OperationResult.failure("Kontot hittades inte", ErrorCode.ACCOUNT_NOT_FOUND);
        }

        if (account.getBalance() >= amount) {
            return OperationResult.success("Tillräckligt saldo tillgängligt");
        } else {
            return OperationResult.failure("Otillräckligt saldo. Tillgängligt: " + account.getBalance() + " kr, Begärt: " + amount + " kr", ErrorCode.INSUFFICIENT_FUNDS);
        }
    }

    /**
     * Tar ut ett belopp från ett konto om det finns tillräckligt med saldo.
     * Metoden validerar beloppet och kontots saldo innan uttaget genomförs.
     *
     * @param accountNumber Kontonumret för kontot
     * @param amount Beloppet som ska tas ut
     * @return TransactionResult med information om uttaget lyckades eller varför det misslyckades
     */
    public TransactionResult withdraw(String accountNumber, double amount) {
        // Kontrollera att beloppet är positivt
        if (amount <= 0) {
            return TransactionResult.failure("Belopp måste vara större än noll", ErrorCode.INVALID_AMOUNT);
        }

        // Hämta kontot
        Account account = getAccount(accountNumber);
        if (account == null) {
            return TransactionResult.failure("Kontot hittades inte", ErrorCode.ACCOUNT_NOT_FOUND);
        }

        // Kontrollera om det finns tillräckligt med saldo
        if (account.getBalance() < amount) {
            return TransactionResult.failure("Otillräckligt saldo. Tillgängligt: " + account.getBalance() + " kr", ErrorCode.INSUFFICIENT_FUNDS);
        }

        // Uppdatera saldot (minskar med uttagsbeloppet)
        Account updatedAccount = updatedBalance(accountNumber, account.getBalance() - amount);
        return TransactionResult.success(updatedAccount.getBalance());
    }

    /**
     * Metod för att sätta in pengar på ett konto
     * @param accountNumber – kontot att sätta in pengar på
     * @param notes – en karta med sedelvalörer och antal (t.ex. {500=2, 100=3})
     * @param confirmed – true om användaren bekräftat insättningen
     * @return TransactionResult med information om insättningen lyckades eller varför den misslyckades
     */
    public TransactionResult deposit(String accountNumber, Map<Integer, Integer> notes, boolean confirmed) {
        // Hämta kontot via AccountService
        Account account = getAccount(accountNumber);
        if (account == null) {
            return TransactionResult.failure("Kontot hittades inte", ErrorCode.ACCOUNT_NOT_FOUND);
        }

        // Kontrollera om insättningen är bekräftad av användaren
        if (!confirmed) {
            return TransactionResult.failure("Insättning avbruten – ej bekräftad", ErrorCode.VALIDATION_ERROR);
        }

        try {
            // Räkna ihop summan av sedlarna
            int amount = 0;
            // Går igenom varje post i Map:en där nyckeln är sedelns valör (t.ex. 100 kr)
            // och värdet är hur många sådana sedlar det finns
            for (Map.Entry<Integer, Integer> entry : notes.entrySet()) {
                int denomination = entry.getKey();   // t.ex. 100, 200, 500
                int count = entry.getValue();        // hur många sedlar av den valören

                // Kontrollera att valören är giltig
                boolean isValid = false;
                for (int validDenomination : com.bank.util.BankConstants.VALID_DENOMINATIONS) {
                    if (denomination == validDenomination) {
                        isValid = true;
                        break;
                    }
                }

                // Om inte, returnera fel
                if (!isValid) {
                    return TransactionResult.failure("Ogiltig sedelvalör: " + denomination, ErrorCode.INVALID_AMOUNT);
                }

                // Lägg till värdet av sedlarna i totalsumman
                amount += denomination * count;
            }

            // Uppdatera kontots saldo med det nya beloppet
            Account updatedAccount = updatedBalance(accountNumber, account.getBalance() + amount);

            // Logga insättningen
            System.out.println("Loggad insättning: " + amount + " kr till konto " + accountNumber);

            // Skriv ut kvittoinformation (simulerat)
            System.out.println("Insättning av " + amount + " kr klar.");
            System.out.println("Vill du ha kvitto? (Simuleras)");

            return TransactionResult.success(updatedAccount.getBalance());

        } catch (Exception e) {
            return TransactionResult.failure("Fel vid insättning: " + e.getMessage(), ErrorCode.INVALID_AMOUNT);
        }
    }

    /**
     * Hämtar det formaterade saldot för ett konto.
     *
     * @param accountNumber Kontonumret för kontot
     * @return Formaterat saldo som en sträng om kontot finns, annars null
     */
    public String getFormattedBalance(String accountNumber) {
        // Hämta kontot
        Account account = getAccount(accountNumber);

        // Returnera formaterat saldo om kontot finns
        return account != null ? account.getFormattedBalance() : null; // ternär operation
    }
}