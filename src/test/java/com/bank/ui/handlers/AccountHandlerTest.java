package com.bank.ui.handlers;

import com.bank.model.Account;
import com.bank.service.account.AccountService;
import com.bank.ui.MockUserInterface;
import com.bank.repository.AccountRepository;
import com.bank.repository.InMemoryAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testklass för AccountHandler med UserInterface.
 * Testar UI-interaktion för kontohantering - INTE business logic som redan testas i service-testerna.
 *
 * Fokus: Testar att användaren kan navigera menyer, välja konton och se meddelanden.
 */
class AccountHandlerTest {

    private AccountHandler accountHandler;
    private MockUserInterface mockUI;
    private AccountService accountService;
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        mockUI = new MockUserInterface();
        accountRepository = new InMemoryAccountRepository();
        accountService = new AccountService(accountRepository);
        accountHandler = new AccountHandler(mockUI, accountService);

        // Skapa testkonton
        Account account1 = new Account("1001", "Lönekonto", 1500.0);
        Account account2 = new Account("1002", "Sparkonto", 5000.0);
        accountRepository.saveAccount(account1);
        accountRepository.saveAccount(account2);

        // Koppla konton till kort
        accountRepository.linkAccountToCard("1001", "123456789012");
        accountRepository.linkAccountToCard("1002", "123456789012");

        // Sätt autentiserat kort
        accountHandler.setAuthenticatedCardNumber("123456789012");
    }

    @Test
    void shouldShowBalanceForSelectedAccount() {
        // Testar: UI-flöde för att visa saldo
        // SKILLNAD från service-test: Testar användarval via UI, inte bara getBalance()

        // Arrange - förkonfigurera att användaren väljer konto 1
        mockUI.setInputs("1");

        // Act
        accountHandler.showBalance();

        // Assert - verifierar att rätt meddelande visas för användaren
        assertTrue(mockUI.hasMessage("Saldo på konto 1001: 1 500,00 kr"));
    }

    @Test
    void shouldShowErrorForInvalidAccountSelection() {
        // Testar: UI-felhantering vid ogiltig inmatning
        // Detta kan INTE testas i service-testerna - det är UI-specifik logik

        // Arrange - förkonfigurera ogiltigt val
        mockUI.setInputs("99");

        // Act
        Account selected = accountHandler.selectAccount();

        // Assert
        assertNull(selected);
        assertTrue(mockUI.hasError("Ogiltigt val."));
    }

    @Test
    void shouldListAvailableAccounts() {
        // Testar: UI-menyn för kontoval
        // Service-testet testar findByCardNumber(), detta testar UI-presentationen

        // Arrange - förkonfigurera val av konto 2
        mockUI.setInputs("2");

        // Act
        Account selected = accountHandler.selectAccount();

        // Assert - verifierar både korrekt val OCH att menyn visas
        assertNotNull(selected);
        assertEquals("1002", selected.getAccountNumber());
        assertTrue(mockUI.hasMessage("Välj konto:"));
        assertTrue(mockUI.hasMessage("1. Konto 1001 (Lönekonto)"));
        assertTrue(mockUI.hasMessage("2. Konto 1002 (Sparkonto)"));
    }

    @Test
    void shouldShowMessageWhenNoAccountsAvailable() {
        // Testar: UI-meddelande när inga konton finns
        // Service-testet testar tom lista, detta testar användarmeddelandet

        // Arrange - inget autentiserat kort satt
        accountHandler.setAuthenticatedCardNumber("999999999999");

        // Act
        Account selected = accountHandler.selectAccount();

        // Assert
        assertNull(selected);
        assertTrue(mockUI.hasMessage("Inga konton tillgängliga."));
    }

    @Test
    void shouldHandleNonNumericInput() {
        // Testar: UI-hantering av icke-numerisk input
        // Rent UI-specifik test - kan inte testas på service-nivå

        // Arrange
        mockUI.setInputs("abc");

        // Act
        Account selected = accountHandler.selectAccount();

        // Assert
        assertNull(selected);
        assertTrue(mockUI.hasError("Ogiltigt val."));
    }

    @Test
    void shouldHandleValidSelectionAtBoundaries() {
        // Testar: UI-hantering vid gränsfall (första och sista kontot)

        // Test första kontot
        mockUI.clearHistory();
        mockUI.setInputs("1");
        Account first = accountHandler.selectAccount();
        assertEquals("1001", first.getAccountNumber());

        // Test sista kontot
        mockUI.clearHistory();
        mockUI.setInputs("2");
        Account last = accountHandler.selectAccount();
        assertEquals("1002", last.getAccountNumber());
    }
}