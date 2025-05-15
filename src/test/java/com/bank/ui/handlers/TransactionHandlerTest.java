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
 * Testklass för TransactionHandler med UserInterface.
 * Testar UI-interaktion för transaktioner - INTE business logic som redan testas i service-testerna.
 *
 * Fokus: Testar användarflöden, bekräftelser, kvitton och UI-felhantering.
 */
class TransactionHandlerTest {

    private TransactionHandler transactionHandler;
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
        transactionHandler = new TransactionHandler(mockUI, accountHandler);

        // Skapa testkonto
        Account account = new Account("1001", "Testkonto", 1000.0);
        accountRepository.saveAccount(account);
        accountRepository.linkAccountToCard("1001", "123456789012");
        accountHandler.setAuthenticatedCardNumber("123456789012");
    }

    // =============================================================================
    // UTTAG TESTER - Testar UI-flöden som service-testerna INTE täcker
    // =============================================================================

    @Test
    void shouldHandleSuccessfulWithdrawal() {
        // Testar: Hela UI-flödet för uttag (val, bekräftelse, meddelanden)
        // SKILLNAD från service-test: Testar användarinteraktion, inte bara withdraw()

        // Arrange
        mockUI.setInputs("1", "500"); // Välj konto 1, ta ut 500 kr
        mockUI.setConfirmationAnswers(true, false); // Bekräfta uttag, nej till kvitto

        // Act
        transactionHandler.handleWithdrawal();

        // Assert - verifierar UI-meddelanden OCH business logic
        assertTrue(mockUI.hasMessage("Uttag genomfört. Ta dina pengar."));

        // Verifiera att saldot uppdaterats
        Account updatedAccount = accountService.getAccount("1001");
        assertEquals(500.0, updatedAccount.getBalance());
    }

    @Test
    void shouldHandleWithdrawalWithReceipt() {
        // Testar: UI-flöde för kvitto (service-testerna testar inte kvittofunktionalitet)

        // Arrange
        mockUI.setInputs("1", "300"); // Välj konto 1, ta ut 300 kr
        mockUI.setConfirmationAnswers(true, true); // Bekräfta uttag, ja till kvitto

        // Act
        transactionHandler.handleWithdrawal();

        // Assert - verifierar kvittoinformation visas korrekt
        assertTrue(mockUI.hasMessage("Uttag genomfört. Ta dina pengar."));
        assertTrue(mockUI.hasMessage("Kvitto: Du tog ut 300.0 kr från konto 1001"));
        assertTrue(mockUI.hasMessage("Nytt saldo: 700,00 kr"));
    }

    @Test
    void shouldHandleCancelledWithdrawal() {
        // Testar: UI-flöde när användaren avbryter
        // Service-testerna kan inte testa "avbryt"-funktionalitet

        // Arrange
        mockUI.setInputs("1", "500"); // Välj konto 1, ta ut 500 kr
        mockUI.setConfirmationAnswers(false); // Avbryt uttag

        // Act
        transactionHandler.handleWithdrawal();

        // Assert
        assertTrue(mockUI.hasMessage("Uttag avbrutet."));

        // Verifiera att saldot är oförändrat
        Account account = accountService.getAccount("1001");
        assertEquals(1000.0, account.getBalance());
    }

    @Test
    void shouldHandleInvalidWithdrawalAmount() {
        // Testar: UI-hantering av ogiltigt belopp
        // Service-testerna testar NumberFormatException, detta testar UI-responsen

        // Arrange
        mockUI.setInputs("1", "abc"); // Välj konto 1, ogiltigt belopp

        // Act
        transactionHandler.handleWithdrawal();

        // Assert
        assertTrue(mockUI.hasError("Ogiltigt belopp. Försök igen."));
    }

    @Test
    void shouldShowCurrentBalanceBeforeWithdrawal() {
        // Testar: UI visar aktuellt saldo före uttag
        // Denna UX-funktion testas inte i service-testerna

        // Arrange
        mockUI.setInputs("1", "100");
        mockUI.setConfirmationAnswers(true, false);

        // Act
        transactionHandler.handleWithdrawal();

        // Assert
        assertTrue(mockUI.hasMessage("Aktuellt saldo: 1 000,00 kr"));
    }

    // =============================================================================
    // INSÄTTNING TESTER - Testar UI-flöden för insättningar
    // =============================================================================

    @Test
    void shouldHandleSuccessfulDeposit() {
        // Testar: UI-flöde för insättning med sedlar
        // Service-testerna testar deposit(), detta testar inmatning av sedlar via UI

        // Arrange
        mockUI.setInputs("1", "2", "1", "0"); // Välj konto 1, 2x100kr, 1x200kr, 0x500kr
        mockUI.setConfirmationAnswers(true, false); // Bekräfta insättning, nej till kvitto

        // Act
        transactionHandler.handleDeposit();

        // Assert - verifierar UI-meddelanden
        assertTrue(mockUI.hasMessage("Totalt att sätta in: 400 kr"));

        // Verifiera att saldot uppdaterats
        Account updatedAccount = accountService.getAccount("1001");
        assertEquals(1400.0, updatedAccount.getBalance());
    }

    @Test
    void shouldHandleDepositWithReceipt() {
        // Testar: UI-flöde för insättning med kvitto

        // Arrange
        mockUI.setInputs("1", "0", "0", "1"); // Välj konto 1, 0x100kr, 0x200kr, 1x500kr
        mockUI.setConfirmationAnswers(true, true); // Bekräfta insättning, ja till kvitto

        // Act
        transactionHandler.handleDeposit();

        // Assert - verifierar kvittoinformation
        assertTrue(mockUI.hasMessage("Kvitto: Du satte in 500 kr på konto 1001"));
        assertTrue(mockUI.hasMessage("Nytt saldo: 1500,00 kr"));
    }

    @Test
    void shouldHandleCancelledDeposit() {
        // Testar: UI-flöde när insättning avbryts

        // Arrange
        mockUI.setInputs("1", "1", "0", "0"); // Välj konto 1, 1x100kr
        mockUI.setConfirmationAnswers(false); // Avbryt insättning

        // Act
        transactionHandler.handleDeposit();

        // Assert
        assertTrue(mockUI.hasErrorContaining("Insättningen misslyckades: Insättning avbruten"));

        // Verifiera att saldot är oförändrat
        Account account = accountService.getAccount("1001");
        assertEquals(1000.0, account.getBalance());
    }

    @Test
    void shouldHandleInvalidNoteCount() {
        // Testar: UI-hantering av ogiltigt antal sedlar
        // Service-testerna testar inte UI-validering av sedel-input

        // Arrange
        mockUI.setInputs("1", "abc", "0", "0"); // Välj konto 1, ogiltigt antal 100kr-sedlar

        // Act
        transactionHandler.handleDeposit();

        // Assert
        assertTrue(mockUI.hasError("Ogiltigt antal för 100 kr sedlar."));
    }

    @Test
    void shouldShowNoteDenominationPrompts() {
        // Testar: UI visar prompter för alla valörer
        // Verifierar att UI-prompterna för sedlar visas korrekt

        // Arrange
        mockUI.setInputs("1", "0", "0", "0"); // Välj konto 1, inga sedlar

        // Act
        transactionHandler.handleDeposit();

        // Assert - verifierar att alla sedeltyper frågas efter
        assertTrue(mockUI.hasMessageContaining("100 kr:"));
        assertTrue(mockUI.hasMessageContaining("200 kr:"));
        assertTrue(mockUI.hasMessageContaining("500 kr:"));
    }

    // =============================================================================
    // GEMENSAMMA TESTER
    // =============================================================================

    @Test
    void shouldReturnEarlyIfNoAccountSelected() {
        // Testar: UI-hantering när inget konto väljs

        // Arrange - mockUI returnerar null när inget konto väljs
        accountHandler.setAuthenticatedCardNumber("999999999999"); // Inga konton

        // Act
        transactionHandler.handleWithdrawal();

        // Assert - ingen transaktion ska ske, inga fel
        Account account = accountService.getAccount("1001");
        assertEquals(1000.0, account.getBalance()); // Oförändrat
    }
}