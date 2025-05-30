package com.bank.service;

import com.bank.model.Account;
import com.bank.repository.AccountRepository;
import com.bank.repository.InMemoryAccountRepository;
import com.bank.service.account.AccountService;
import com.bank.service.validation.ErrorCode;
import com.bank.service.transaction.OperationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testklass för AccountService.
 *
 * Testklassen använder en InMemoryAccountRepository för att isolera testerna från externa
 * beroenden och fokuseras enbart på att verifiera AccountService-logiken.
 *
 * Uppdaterad för att använda nya Result-klasser för bättre felhantering.
 */

public class AccountServiceTest {

    private AccountRepository accountRepository;
    private AccountService accountService;

    /**
     * Förbereder testobjekt och testdata innan varje test.
     *
     * Skapar en ny instans av InMemoryAccountRepository och AccountService.
     * Konfigurerar repository med två testkonton som används i testerna.
     */
    @BeforeEach
    void setUp() {
        // Arrange - Förbered test-objekt
        accountRepository = new InMemoryAccountRepository();
        accountService = new AccountService(accountRepository);

        // Skapa testkonton
        Account account1 = new Account("12345", "Lönekonto", 5000.0);
        Account account2 = new Account("67890", "Sparkonto", 10000.0);

        accountRepository.saveAccount(account1);
        accountRepository.saveAccount(account2);
    }

    /**
     * Testar att getAccount-metoden korrekt hämtar ett existerande konto.
     *
     * Verifierar att konto-objektet innehåller korrekt kontonummer, namn och saldo.
     */
    @Test
    void shouldReturnAccountByAccountNumber() {
        // Act - Anropa metoden som testas
        Account account = accountService.getAccount("12345");

        // Assert - Verifiera resultatet
        assertNotNull(account, "Account cannot be null");
        assertEquals("12345", account.getAccountNumber(), "Account should match");
        assertEquals("Lönekonto", account.getAccountName(), "Account name should match");
        assertEquals(5000.0, account.getBalance(), "Account balance should match");
    }

    /**
     * Testar att getAccount-metoden returnerar null när ett icke-existerande konto efterfrågas.
     *
     * Detta test säkerställer att systemet hanterar saknade konton korrekt.
     */
    @Test
    void shouldReturnNullForNonExistentAccount() {
        // Act - Anropa metoden med ett ogiltigt kontonummer
        Account account = accountService.getAccount("99999");

        // Assert - Verifiera att resultatet är null
        assertNull(account, "Method should return null for non existent account");
    }

    /**
     * Testar att updateBalance-metoden korrekt uppdaterar saldot på ett konto.
     *
     * Verifierar att:
     * 1. Metoden returnerar ett uppdaterat konto-objekt
     * 2. Det uppdaterade kontot har korrekt saldo
     * 3. Ändringen har sparats i repository
     */
    @Test
    void shouldUpdateAccountBalance() {
        // Arrange - Förbered testdata
        String accountNumber = "12345";
        double newBalance = 7000.0;

        // Act - Anropa metoden som testas
        Account updatedAccount = accountService.updatedBalance(accountNumber, newBalance);

        // Assert - Verifiera resultatet
        assertNotNull(updatedAccount, "Updated account should not be null");
        assertEquals(newBalance, updatedAccount.getBalance(), "Balance should update to new value");

        // Verifiera att kontot har uppdaterats i repositoryt
        Account retrievedAccount = accountRepository.findByAccountNumber(accountNumber);
        assertEquals(newBalance, retrievedAccount.getBalance(), "Balance should update in repository");
    }

    /**
     * Testar att updateBalance-metoden returnerar null när man försöker uppdatera ett icke-existerande konto.
     *
     * Detta test säkerställer att systemet hanterar uppdateringsförsök för saknade konton korrekt.
     */
    @Test
    void shouldReturnNullWhenUpdatingNonExistentAccount() {
        // Act - Anropa metoden med ett ogiltigt kontonummer
        Account updatedAccount = accountService.updatedBalance("99999", 1000.0);

        // Assert - Verifiera att resultatet är null
        assertNull(updatedAccount, "Method should return null for non existent account");
    }

    /**
     * Testar att accountExists-metoden korrekt identifierar om ett konto finns eller inte.
     * Uppdaterad för att använda OperationResult istället för boolean.
     *
     * Verifierar:
     * 1. Metoden returnerar success för existerande konton
     * 2. Metoden returnerar failure med rätt error code för icke-existerande konton
     */
    @Test
    void shouldCheckIfAccountExists() {
        // Act & Assert för existerande konto
        OperationResult existingAccountResult = accountService.accountExists("12345");
        assertTrue(existingAccountResult.isSuccess(), "Should return success for existing account");
        assertEquals("Kontot existerar", existingAccountResult.getMessage());

        // Act & Assert för icke-existerande konto
        OperationResult nonExistentAccountResult = accountService.accountExists("99999");
        assertFalse(nonExistentAccountResult.isSuccess(), "Should return failure for non existing account");
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, nonExistentAccountResult.getErrorCode());
        assertEquals("Kontot hittades inte", nonExistentAccountResult.getMessage());
    }

    /**
     * Testar att hasEnoughBalance-metoden korrekt bedömer om ett konto har tillräckligt med saldo.
     * Uppdaterad för att använda OperationResult istället för boolean.
     *
     * Testar flera olika scenarion:
     * 1. Konto med mer saldo än det efterfrågade beloppet
     * 2. Konto med exakt samma saldo som det efterfrågade beloppet
     * 3. Konto med mindre saldo än det efterfrågade beloppet
     * 4. Icke-existerande konto
     */
    @Test
    void shouldVerifyIfAccountCanAffordRequestedAmount() {
        // Test Case 1: Kontot har mer pengar än begärt belopp (5000 > 3000)
        OperationResult sufficientResult = accountService.hasEnoughBalance("12345", 3000.0);
        assertTrue(sufficientResult.isSuccess(), "Ska returnera success när saldot är tillräckligt");
        assertEquals("Tillräckligt saldo tillgängligt", sufficientResult.getMessage());

        // Test Case 2: Kontot har exakt samma belopp som begärt (5000 = 5000)
        OperationResult exactResult = accountService.hasEnoughBalance("12345", 5000.0);
        assertTrue(exactResult.isSuccess(), "Ska returnera success när saldot är exakt lika med begärt belopp");

        // Test Case 3: Kontot har mindre pengar än begärt belopp (5000 < 6000)
        OperationResult insufficientResult = accountService.hasEnoughBalance("12345", 6000.0);
        assertFalse(insufficientResult.isSuccess(), "Ska returnera failure när saldot är otillräckligt");
        assertEquals(ErrorCode.INSUFFICIENT_FUNDS, insufficientResult.getErrorCode());
        assertTrue(insufficientResult.getMessage().contains("Otillräckligt saldo"));

        // Test Case 4: Kontot existerar inte
        OperationResult nonExistentResult = accountService.hasEnoughBalance("99999", 1000.0);
        assertFalse(nonExistentResult.isSuccess(), "Ska returnera failure för icke-existerande konto");
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, nonExistentResult.getErrorCode());
        assertEquals("Kontot hittades inte", nonExistentResult.getMessage());
    }

    /**
     * Testar att getFormattedBalance-metoden returnerar saldot i korrekt format.
     *
     * Verifierar att saldot formateras med korrekt valutasymbol, tusentalsavgränsare och decimaltecken.
     */
    @Test
    void shouldReturnFormattedBalance() {
        // Act - Anropa metoden som testas
        String formattedBalance = accountService.getFormattedBalance("12345");

        // Assert - Verifiera att formateringen är korrekt
        assertEquals("5 000,00 kr", formattedBalance, "Saldot ska formateras korrekt med valutasymbol");
    }

    /**
     * Testar att getFormattedBalance-metoden returnerar null för ett icke-existerande konto.
     *
     * Detta test säkerställer att systemet hanterar formatering av saknade konton korrekt.
     */
    @Test
    void shouldReturnNullWhenGettingFormattedBalanceForNonExistentAccount() {
        // Act - Anropa metoden med ett ogiltigt kontonummer
        String formattedBalance = accountService.getFormattedBalance("99999");

        // Assert - Verifiera att resultatet är null
        assertNull(formattedBalance, "Should return null for non existent account");
    }
}