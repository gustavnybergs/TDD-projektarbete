package com.bank.repository;
// Testar lagring och hämtning av konton

import com.bank.model.Account;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AccountRepositoryTest {
    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp() {
        // Vi använder en enkel implementation för testerna
        accountRepository = new InMemoryAccountRepository();
    }

    /**
     * Testar att ett konto kan sparas och hämtas från repositoryt.
     * Verifierar att kontot som hämtas har samma värden som det som sparades.
     */
    @Test
    public void shouldSaveAndRetrieveAccount() {
        // Arrange
        Account account = new Account("12345", "Lönekonto", 5000.0);

        // Act
        accountRepository.saveAccount(account);
        Account retrievedAccount = accountRepository.findByAccountNumber("12345");

        // Assert
        assertNotNull(retrievedAccount);
        assertEquals("12345", retrievedAccount.getAccountNumber());
        assertEquals("Lönekonto", retrievedAccount.getAccountName());
        assertEquals(5000.0, retrievedAccount.getBalance());
    }

    /**
     * Testar att null returneras när ett icke-existerande kontonummer efterfrågas.
     * Verifierar att systemet hanterar fallet med saknade konton korrekt.
     */
    @Test
    public void shouldReturnNullForNonExistingAccount() {
        // Act
        Account retrievedAccount = accountRepository.findByAccountNumber("nonexistent");

        // Assert
        assertNull(retrievedAccount);
    }

    /**
     * Testar att konton kan kopplas till ett kort och hämtas via kortnummer.
     * Verifierar att rätt konton returneras för det angivna kortet.
     */
    @Test
    public void shouldLinkAccountsToCardAndRetrieveThem() {
        // Arrange
        Account account1 = new Account("12345", "Lönekonto", 5000.0);
        Account account2 = new Account("67890", "Sparkonto", 10000.0);
        accountRepository.saveAccount(account1);
        accountRepository.saveAccount(account2);

        // Act
        accountRepository.linkAccountToCard("12345", "123456789012");
        accountRepository.linkAccountToCard("67890", "123456789012");
        List<Account> accounts = accountRepository.findByCardNumber("123456789012");

        // Assert
        assertEquals(2, accounts.size());
        assertTrue(accounts.stream().anyMatch(account -> account.getAccountNumber().equals("12345")));
        assertTrue(accounts.stream().anyMatch(account -> account.getAccountNumber().equals("67890")));
        // lambda-funktion som tar ett Account-objekt (a) och kontrollerar om dess kontonummer är "12345/6890"
    }

    /**
     * Testar att en tom lista returneras när ett kort utan kopplade konton används.
     * Verifierar att systemet hanterar fallet med saknade kopplingar korrekt.
     */
    @Test
    public void shouldReturnEmptyListForCardWithNoAccounts() {
        // Act
        List<Account> accounts = accountRepository.findByCardNumber("nonexistent");

        // Assert
        assertNotNull(accounts);
        assertTrue(accounts.isEmpty());
    }
}
