package com.bank.service;

import com.bank.model.Account;
import com.bank.repository.AccountRepository;
import com.bank.repository.InMemoryAccountRepository;
import com.bank.service.account.AccountService;
import com.bank.service.validation.ErrorCode;
import com.bank.service.transaction.TransactionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testklass för uttagsfunktionalitet i AccountService.
 * Testar alla aspekter av withdraw-metoden inklusive
 * giltiga uttag och felhantering.
 *
 * Uppdaterad för att använda nya TransactionResult-klasser.
 */
class AccountServiceWithdrawalTest {

    private AccountRepository accountRepository;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        // Initiera ett nytt repository och en ny service för varje test
        accountRepository = new InMemoryAccountRepository();
        accountService = new AccountService(accountRepository);

        // Skapa ett testkonto med 1000 kr
        Account testAccount = new Account("1234", "Testkonto", 1000.0);
        accountRepository.saveAccount(testAccount);
    }

    /**
     * Testar att ett giltigt uttag minskar saldot korrekt.
     * Uppdaterad för att använda TransactionResult.
     */
    @Test
    void withdraw_shouldReduceBalance_whenAmountIsValid() {
        // Act
        TransactionResult result = accountService.withdraw("1234", 300.0);

        // Assert - Kontrollera att transaktionen lyckades
        assertTrue(result.isSuccess(), "Uttaget ska lyckas");

        // Kontrollera att det nya saldot returneras
        assertTrue(result.getNewBalance().isPresent(), "Nytt saldo ska finnas tillgängligt");
        assertEquals(700.0, result.getNewBalance().get(), "Nytt saldo ska vara 700.0");

        // Verifiera att kontot har uppdaterats i repositoryt
        Account updatedAccount = accountService.getAccount("1234");
        assertEquals(700.0, updatedAccount.getBalance(), "Konto i repository ska ha uppdaterat saldo");
    }

    /**
     * Testar att ett negativt belopp resulterar i en felaktig TransactionResult.
     * Uppdaterad för att använda TransactionResult istället för exception.
     */
    @Test
    void withdraw_shouldReturnFailure_whenAmountIsNegative() {
        // Act
        TransactionResult result = accountService.withdraw("1234", -100.0);

        // Assert
        assertFalse(result.isSuccess(), "Uttag med negativt belopp ska misslyckas");
        assertEquals(ErrorCode.INVALID_AMOUNT, result.getErrorCode(), "Felkod ska vara INVALID_AMOUNT");
        assertEquals("Belopp måste vara större än noll", result.getMessage(), "Felmeddelande ska vara korrekt");

        // Verifiera att saldot är oförändrat
        Account account = accountService.getAccount("1234");
        assertEquals(1000.0, account.getBalance(), "Saldot ska vara oförändrat");
    }

    /**
     * Testar att ett uttag större än saldot resulterar i en felaktig TransactionResult.
     * Uppdaterad för att använda TransactionResult istället för exception.
     */
    @Test
    void withdraw_shouldReturnFailure_whenBalanceIsInsufficient() {
        // Act
        TransactionResult result = accountService.withdraw("1234", 1200.0);

        // Assert
        assertFalse(result.isSuccess(), "Uttag med otillräckligt saldo ska misslyckas");
        assertEquals(ErrorCode.INSUFFICIENT_FUNDS, result.getErrorCode(), "Felkod ska vara INSUFFICIENT_FUNDS");
        assertTrue(result.getMessage().contains("Otillräckligt saldo"), "Felmeddelande ska innehålla 'Otillräckligt saldo'");
        assertTrue(result.getMessage().contains("1000.0"), "Felmeddelande ska visa tillgängligt saldo");

        // Verifiera att saldot är oförändrat
        Account account = accountService.getAccount("1234");
        assertEquals(1000.0, account.getBalance(), "Saldot ska vara oförändrat");
    }

    /**
     * Testar att ett uttag från ett icke-existerande konto returnerar failure.
     * Uppdaterad för att använda TransactionResult.
     */
    @Test
    void withdraw_shouldReturnFailure_whenAccountDoesNotExist() {
        // Act
        TransactionResult result = accountService.withdraw("9999", 100.0);

        // Assert
        assertFalse(result.isSuccess(), "Uttag från icke-existerande konto ska misslyckas");
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, result.getErrorCode(), "Felkod ska vara ACCOUNT_NOT_FOUND");
        assertEquals("Kontot hittades inte", result.getMessage(), "Felmeddelande ska vara korrekt");
    }

    /**
     * Testar att ett uttag av exakt hela saldot är tillåtet.
     * Uppdaterad för att använda TransactionResult.
     */
    @Test
    void withdraw_shouldAllowWithdrawingExactBalance() {
        // Act
        TransactionResult result = accountService.withdraw("1234", 1000.0);

        // Assert
        assertTrue(result.isSuccess(), "Uttag av hela saldot ska lyckas");
        assertTrue(result.getNewBalance().isPresent(), "Nytt saldo ska finnas tillgängligt");
        assertEquals(0.0, result.getNewBalance().get(), "Nytt saldo ska vara 0.0");

        // Verifiera att kontot har uppdaterats i repositoryt
        Account updatedAccount = accountService.getAccount("1234");
        assertEquals(0.0, updatedAccount.getBalance(), "Saldot i repository ska vara 0.0");
    }

    /**
     * Testar att ett uttag med belopp noll resulterar i fel.
     */
    @Test
    void withdraw_shouldReturnFailure_whenAmountIsZero() {
        // Act
        TransactionResult result = accountService.withdraw("1234", 0.0);

        // Assert
        assertFalse(result.isSuccess(), "Uttag med belopp noll ska misslyckas");
        assertEquals(ErrorCode.INVALID_AMOUNT, result.getErrorCode(), "Felkod ska vara INVALID_AMOUNT");
        assertEquals("Belopp måste vara större än noll", result.getMessage(), "Felmeddelande ska vara korrekt");

        // Verifiera att saldot är oförändrat
        Account account = accountService.getAccount("1234");
        assertEquals(1000.0, account.getBalance(), "Saldot ska vara oförändrat");
    }
}