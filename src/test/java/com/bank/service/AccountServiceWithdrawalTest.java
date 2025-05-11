package com.bank.service;

import com.bank.model.Account;
import com.bank.repository.AccountRepository;
import com.bank.repository.InMemoryAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testklass för uttagsfunktionalitet i AccountService.
 * Testar alla aspekter av withdraw-metoden inklusive
 * giltiga uttag och felhantering.
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
     */
    @Test
    void withdraw_shouldReduceBalance_whenAmountIsValid() {
        // Act
        boolean result = accountService.withdraw("1234", 300.0);

        // Assert
        assertTrue(result);
        Account updatedAccount = accountService.getAccount("1234");
        assertEquals(700.0, updatedAccount.getBalance());
    }

    /**
     * Testar att ett negativt belopp resulterar i ett exception.
     */
    @Test
    void withdraw_shouldThrowException_whenAmountIsNegative() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.withdraw("1234", -100.0)
        );

        assertEquals("Belopp måste vara större än noll", exception.getMessage());
    }

    /**
     * Testar att ett uttag större än saldot resulterar i ett exception.
     */
    @Test
    void withdraw_shouldThrowException_whenBalanceIsInsufficient() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.withdraw("1234", 1200.0)
        );

        assertEquals("Otillräckligt saldo", exception.getMessage());
    }

    /**
     * Testar att ett uttag från ett icke-existerande konto returnerar false.
     */
    @Test
    void withdraw_shouldReturnFalse_whenAccountDoesNotExist() {
        // Act
        boolean result = accountService.withdraw("9999", 100.0);

        // Assert
        assertFalse(result);
    }

    /**
     * Testar att ett uttag av exakt hela saldot är tillåtet.
     */
    @Test
    void withdraw_shouldAllowWithdrawingExactBalance() {
        // Act
        boolean result = accountService.withdraw("1234", 1000.0);

        // Assert
        assertTrue(result);
        Account updatedAccount = accountService.getAccount("1234");
        assertEquals(0.0, updatedAccount.getBalance());
    }
}
