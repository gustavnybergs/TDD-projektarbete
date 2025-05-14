package com.bank.service;

import com.bank.model.Account;
import com.bank.repository.AccountRepository;
import com.bank.repository.InMemoryAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testklass för AccountService deposit funktionalitet.
 * Uppdaterad för att testa deposit-metoden i AccountService istället för DepositService.
 */
class AccountServiceDepositTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        // Initiera repository och service
        AccountRepository repository = new InMemoryAccountRepository();
        accountService = new AccountService(repository);

        // Skapa testkonto
        Account testAccount = new Account("1234", "Test Account", 1000.0);
        repository.saveAccount(testAccount);
    }

    /**
     * Testar att bekräftad insättning lyckas och uppdaterar saldot korrekt.
     */
    @Test
    void testDepositConfirmed() {
        // Arrange
        Map<Integer, Integer> notes = Map.of(100, 2, 200, 1); // 400 kr totalt

        // Act
        TransactionResult result = accountService.deposit("1234", notes, true);

        // Assert
        assertTrue(result.isSuccess(), "Confirmed deposit should succeed");
        assertTrue(result.getNewBalance().isPresent(), "New balance should be available");
        assertEquals(1400.0, result.getNewBalance().get(), "New balance should be 1400.0");

        // Verifiera genom att hämta kontot igen
        Account updatedAccount = accountService.getAccount("1234");
        assertEquals(1400.0, updatedAccount.getBalance(), "Balance in repository should be updated");
    }

    /**
     * Testar att ej bekräftad insättning avbryts och inte ändrar saldot.
     */
    @Test
    void testDepositNotConfirmed() {
        // Arrange
        Map<Integer, Integer> notes = Map.of(500, 1); // 500 kr

        // Act
        TransactionResult result = accountService.deposit("1234", notes, false);

        // Assert
        assertFalse(result.isSuccess(), "Unconfirmed deposit should fail");
        assertEquals(ErrorCode.VALIDATION_ERROR, result.getErrorCode(), "Error code should be VALIDATION_ERROR");
        assertEquals("Insättning avbruten – ej bekräftad", result.getMessage(), "Error message should be correct");

        // Verifiera att saldot är oförändrat
        Account unchangedAccount = accountService.getAccount("1234");
        assertEquals(1000.0, unchangedAccount.getBalance(), "Balance should not change for cancelled deposit");
    }

    /**
     * Testar att insättning med ogiltig valör resulterar i fel.
     */
    @Test
    void testDepositWithInvalidDenominationReturnsFailure() {
        // Arrange
        Map<Integer, Integer> invalidNotes = Map.of(50, 2); // 50 kr är ogiltig

        // Act
        TransactionResult result = accountService.deposit("1234", invalidNotes, true);

        // Assert
        assertFalse(result.isSuccess(), "Deposit with invalid denomination should fail");
        assertEquals(ErrorCode.INVALID_AMOUNT, result.getErrorCode(), "Error code should be INVALID_AMOUNT");
        assertTrue(result.getMessage().contains("Ogiltig sedelvalör"), "Error message should indicate invalid denomination");

        // Verifiera att saldot är oförändrat
        Account unchangedAccount = accountService.getAccount("1234");
        assertEquals(1000.0, unchangedAccount.getBalance(), "Balance should be unchanged after error");
    }

    /**
     * Testar att insättning till icke-existerande konto misslyckas.
     */
    @Test
    void testDepositToNonExistentAccount() {
        // Arrange
        Map<Integer, Integer> notes = Map.of(100, 5); // 500 kr

        // Act
        TransactionResult result = accountService.deposit("9999", notes, true);

        // Assert
        assertFalse(result.isSuccess(), "Deposit to non-existent account should fail");
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, result.getErrorCode(), "Error code should be ACCOUNT_NOT_FOUND");
        assertEquals("Kontot hittades inte", result.getMessage(), "Error message should be correct");
    }

    /**
     * Testar att insättning av tom sedel-map fungerar (0 kr).
     */
    @Test
    void testDepositEmptyNoteMap() {
        // Arrange
        Map<Integer, Integer> emptyMap = Map.of();

        // Act
        TransactionResult result = accountService.deposit("1234", emptyMap, true);

        // Assert
        assertTrue(result.isSuccess(), "Empty deposit should succeed technically");
        assertTrue(result.getNewBalance().isPresent(), "New balance should be available");
        assertEquals(1000.0, result.getNewBalance().get(), "Balance should be unchanged for 0 kr deposit");

        // Verifiera att saldot är oförändrat
        Account accountAfter = accountService.getAccount("1234");
        assertEquals(1000.0, accountAfter.getBalance(), "Account balance should be unchanged");
    }

    /**
     * Testar kombinerade operationer (deposit och withdraw).
     */
    @Test
    void testCombinedDepositAndWithdraw() {
        // Arrange
        Map<Integer, Integer> notes = Map.of(100, 5); // 500 kr

        // Act - först insättning
        TransactionResult depositResult = accountService.deposit("1234", notes, true);

        // Sedan uttag
        TransactionResult withdrawResult = accountService.withdraw("1234", 200.0);

        // Assert
        assertTrue(depositResult.isSuccess(), "Deposit should succeed");
        assertTrue(withdrawResult.isSuccess(), "Withdraw should succeed");

        Account finalAccount = accountService.getAccount("1234");
        assertEquals(1300.0, finalAccount.getBalance(), "Final balance should be 1000 + 500 - 200 = 1300");
    }
}