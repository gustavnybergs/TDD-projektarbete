package com.bank.model;
// Testar Account-modellen och dess funktionalitet

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {

    /**
     * Testar att ett konto skapas korrekt med de värden som skickas in.
     * Verifierar att getters returnerar samma värden som användes vid skapandet.
     */
    @Test
    public void shouldCreateAccountWithCorrectValues () {
        // Arrange & Act
        Account account = new Account("12345", "Lönekonto", 5000.0);

        // Assert
        assertEquals("12345", account.getAccountNumber());
        assertEquals("Lönekonto", account.getAccountName());
        assertEquals(5000.0, account.getBalance());
    }

    /**
     * Testar att saldot formateras korrekt med valutasymbol.
     * Kontrollerar att formateringen följer svensk standard med komma och kronor.
     */
    @Test
    public void shouldFormatBalanceWithCurrencySymbol() {
        // Arrange
        Account account = new Account ("12345", "Lönekonto", 5000.0);

        // Act
        String formattedBalance = account.getFormattedBalance();

        // Assert
        assertEquals("5 000,00 kr", formattedBalance);
    }

    /**
     * Testar att ett konto inte kan skapas med negativt saldo.
     * Verifierar att ett IllegalArgumentException kastas när ett negativt värde anges.
     */
    @Test
    public void shouldNotAllowNegativeBalance() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Account("", "Lönekonto", -100.0);
        });
    }

    /**
     * Testar att kontonumret måste vara giltigt (inte null eller tomt).
     * Verifierar att ett IllegalArgumentException kastas för ogiltiga kontonummer.
     */
    @Test
    public void shouldRequireValidAccountNumber() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Account("", "Lönekonto", 5000.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Account(null, "Lönekonto", 5000.0);
        });
    }

    /**
     * Testar att kontonamnet måste vara giltigt (inte null eller tomt).
     * Verifierar att ett IllegalArgumentException kastas för ogiltiga kontonamn.
     */
    @Test
    public void shouldRequireValidAccountName() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Account("12345", "", 5000.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Account("12345", null, 5000.0);
        });
    }
}
