package com.bank.service;

import com.bank.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testklass för ErrorHandler.
 *
 * Testar funktionalitet för felhantering, validering och felmeddelanden
 * i bankomatsystemet.
 *
 * Uppdaterad för att använda nya Result-klasser.
 */

public class ErrorHandlerTest {
    private ErrorHandler errorHandler;
    private AccountService accountService;

    /**
     * Förbereder testobjekt och testdata innan varje test.
     */
    @BeforeEach
    void setUp() {
        // Skapa en mock av Accountservice för teständamål
        accountService = new AccountServiceMock();
        errorHandler = new ErrorHandler(accountService);
    }

    /**
     * Testar att validering av uttag lyckas när saldot är tillräckligt.
     * Uppdaterad för att använda OperationResult från AccountService.
     */
    @Test
    void shouldValidateWithdrawalWithSufficientBalance() {
        // Arrange - Förbered testdata
        String accountNumber = "12345"; // Konto med saldo 5000 i mock
        double amount = 3000.00;

        // Act - Anropa metoden som testas
        ValidationResult result = errorHandler.validateWithdrawal(accountNumber, amount);

        // Assert - Verifiera resultatet
        assertTrue(result.isValid(), "Validation should succeed when balance is sufficient");
        assertNull(result.getErrorMessage(), "No error message should be seen");
    }

    /**
     * Testar att validering av uttag misslyckas när saldot är otillräckligt.
     * Uppdaterad för att använda OperationResult från AccountService.
     */
    @Test
    void shouldNotValidateWithdrawalWithInsufficientBalance() {
        // Arrange - Förbered testdata
        String accountNumber = "12345"; // Konto med saldo 5000 i mock
        double amount = 6000.00;

        // Act - Anropa metoden som testas
        ValidationResult result = errorHandler.validateWithdrawal(accountNumber, amount);

        // Assert - Verifiera resultatet
        assertFalse(result.isValid(), "Validation should fail when balance is insufficient");
        assertNotNull(result.getErrorMessage(), "Error message should be shown");
        assertTrue(result.getErrorMessage().contains("Insufficient balance"), "Error message should show insufficient balance");
    }

    /**
     * Testar att validering misslyckas för icke-existerande konto.
     */
    @Test
    void shouldNotValidateWithdrawalForNonExistentAccount() {
        // Arrange
        String accountNumber = "99999"; // Icke-existerande konto
        double amount = 1000.00;

        // Act
        ValidationResult result = errorHandler.validateWithdrawal(accountNumber, amount);

        // Assert
        assertFalse(result.isValid(), "Validation should fail for non-existent account");
        assertTrue(result.getErrorMessage().contains("Account does not exist"), "Error message should indicate account doesn't exist");
    }

    /**
     * Testar att validering misslyckas för negativt belopp.
     */
    @Test
    void shouldNotValidateWithdrawalForNegativeAmount() {
        // Arrange
        String accountNumber = "12345";
        double amount = -100.00;

        // Act
        ValidationResult result = errorHandler.validateWithdrawal(accountNumber, amount);

        // Assert
        assertFalse(result.isValid(), "Validation should fail for negative amount");
        assertTrue(result.getErrorMessage().contains("valid amount"), "Error message should mention valid amount");
    }

    /**
     * Testar att ett korrekt nätverksfelmeddelande genereras.
     */
    @Test
    void shouldGenerateNetworkErrorMessage() {
        // Act - Anropa metoden som testas
        String message = errorHandler.getNetworkErrorMessage();

        // Assert - Verifiera resultatet
        assertTrue(message.contains("Network issue"), "Error message should declare network errors");
        assertTrue(message.contains("try again"), "Message should propose customer to try again");
    }

    /**
     * Testar att ett korrekt meddelande för avbruten transaktion genereras.
     */
    @Test
    void shouldGenerateTransactionCancelledMessage() {
        // Act - Anropa metoden som testas
        String message = errorHandler.getTransactionCancelledMessage();

        // Assert - Verifiera resultatet
        assertTrue(message.contains("Transaction cancelled"), "Error message should declare transaction cancelled");
    }

    /**
     * Testar att allvarliga fel loggas korrekt.
     */
    @Test
    void shouldLogError() {
        // Arrange - Förbered testdata
        String errorCode = "E1001";
        String errorMessage = "Database error";

        // Act- Anropa metoden som testas
        boolean logged = errorHandler.logError(errorCode, errorMessage);

        assertTrue(logged, "Method should declare true when logging was successful");
    }

    /**
     * Hjälpklass för att mocka AccountService i tester.
     * Uppdaterad för att returnera OperationResult istället för boolean.
     * Denna klass simulerar att det finns ett konto med nummer "12345" och saldo 5000.
     */
    private class AccountServiceMock extends AccountService {
        public AccountServiceMock() {
            super(null); // Vi använder inte ett riktigt repository i testet
        }

        @Override
        public Account getAccount(String accountNumber) {
            if ("12345".equals(accountNumber)) {
                return new Account("12345", "Testkonto", 5000.0);
            }
            return null;
        }

        @Override
        public OperationResult accountExists(String accountNumber) {
            if ("12345".equals(accountNumber)) {
                return OperationResult.success("Kontot existerar");
            }
            return OperationResult.failure("Kontot hittades inte", ErrorCode.ACCOUNT_NOT_FOUND);
        }

        @Override
        public OperationResult hasEnoughBalance(String accountNumber, double amount) {
            Account account = getAccount(accountNumber);
            if (account == null) {
                return OperationResult.failure("Kontot hittades inte", ErrorCode.ACCOUNT_NOT_FOUND);
            }

            if (account.getBalance() >= amount) {
                return OperationResult.success("Tillräckligt saldo tillgängligt");
            } else {
                return OperationResult.failure("Otillräckligt saldo", ErrorCode.INSUFFICIENT_FUNDS);
            }
        }
    }
}