package com.bank.service;

import com.bank.model.Account;
import com.bank.repository.AccountRepository;
import com.bank.repository.InMemoryAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testklass för deposit-funktionalitet i AccountService.
 * Tidigare DepositServiceTest, nu för AccountService.deposit().
 */
class AccountServiceDepositTest {

    private AccountService accountService;
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        // Skapar en testversion av AccountService (en så kallad mock)
        accountRepository = new InMemoryAccountRepository();
        accountService = new AccountService(accountRepository);

        // Skapar ett testkonto (1000 kr från början)
        Account testAccount = new Account("1234", "Testkonto", 1000.0);
        accountRepository.saveAccount(testAccount);
    }

    @Test
    void testDepositConfirmed() {
        // Simulerar en insättning med 2x100kr och 1x200kr = 400 kr
        Map<Integer, Integer> notes = Map.of(100, 2, 200, 1);

        // true betyder att insättningen bekräftas
        TransactionResult result = accountService.deposit("1234", notes, true);

        // Assert
        assertTrue(result.isSuccess(), "Confirmed deposit should succeed");

        // Kontrollerar att saldot har uppdaterats till 1400 kr
        Account updatedAccount = accountService.getAccount("1234");
        assertEquals(1400.0, updatedAccount.getBalance(), "Saldot bör ha uppdaterats med 400 kr");
    }

    @Test
    void testDepositNotConfirmed() {
        // Simulerar en insättning som INTE bekräftas
        Map<Integer, Integer> notes = Map.of(500, 1); // 500 kr

        // false = insättningen avbryts
        TransactionResult result = accountService.deposit("1234", notes, false);

        // Assert
        assertFalse(result.isSuccess(), "Unconfirmed deposit should fail");
        assertEquals(ErrorCode.VALIDATION_ERROR, result.getErrorCode(), "Error code should be VALIDATION_ERROR");

        // Verifierar att saldot är oförändrat (fortfarande 1000 kr)
        Account unchangedAccount = accountService.getAccount("1234");
        assertEquals(1000.0, unchangedAccount.getBalance(), "Saldot bör inte förändras vid avbruten insättning");
    }

    @Test
    void testDepositWithInvalidDenominationReturnsFailure() {
        // Försöker sätta in ogiltiga sedlar (t.ex. 50 kr som inte accepteras)
        Map<Integer, Integer> invalidNotes = Map.of(50, 2);

        // Använd AccountService nu
        TransactionResult result = accountService.deposit("1234", invalidNotes, true);

        // Assert
        assertFalse(result.isSuccess(), "Deposit with invalid denomination should fail");
        assertEquals(ErrorCode.INVALID_AMOUNT, result.getErrorCode(), "Error code should be INVALID_AMOUNT");
        // Säkerställer att felmeddelandet innehåller rätt text
        assertTrue(result.getMessage().contains("Ogiltig sedelvalör"), "Felmeddelande bör indikera ogiltig valör");

        // Verifiera att saldot är oförändrat
        Account unchangedAccount = accountService.getAccount("1234");
        assertEquals(1000.0, unchangedAccount.getBalance(), "Balance should be unchanged after error");
    }

    @Test
    void testDepositToNonExistentAccount() {
        // Arrange
        Map<Integer, Integer> notes = Map.of(100, 5); // 500 kr

        // Act - använd AccountService nu
        TransactionResult result = accountService.deposit("9999", notes, true);

        // Assert
        assertFalse(result.isSuccess(), "Deposit to non-existent account should fail");
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, result.getErrorCode(), "Error code should be ACCOUNT_NOT_FOUND");
        assertEquals("Kontot hittades inte", result.getMessage(), "Error message should be correct");
    }

    @Test
    void testDepositEmptyNoteMap() {
        // Arrange
        Map<Integer, Integer> emptyMap = new HashMap<>();

        // Act - använd AccountService nu
        TransactionResult result = accountService.deposit("1234", emptyMap, true);

        // Assert
        assertTrue(result.isSuccess(), "Empty deposit should succeed technically");
        assertTrue(result.getNewBalance().isPresent(), "New balance should be available");
        assertEquals(1000.0, result.getNewBalance().get(), "Balance should be unchanged for 0 kr deposit");

        // Verifiera att saldot är oförändrat
        Account accountAfter = accountService.getAccount("1234");
        assertEquals(1000.0, accountAfter.getBalance(), "Account balance should be unchanged");
    }
}