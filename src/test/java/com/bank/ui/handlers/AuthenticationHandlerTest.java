package com.bank.ui.handlers;

import com.bank.model.Card;
import com.bank.service.auth.AuthenticationResult;
import com.bank.service.auth.AuthenticationService;
import com.bank.ui.MockUserInterface;
import com.bank.repository.CardRepository;
import com.bank.repository.InMemoryCardRepository;
import com.bank.util.BankConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testklass för AuthenticationHandler.
 */
class AuthenticationHandlerTest {

    private AuthenticationHandler authHandler;
    private AuthenticationService authService;
    private MockUserInterface mockUI;
    private CardRepository cardRepository;

    @BeforeEach
    void setUp() {
        mockUI = new MockUserInterface();
        cardRepository = new InMemoryCardRepository();
        authService = new AuthenticationService(cardRepository);
        authHandler = new AuthenticationHandler(mockUI, authService);

        // Skapa testkort
        Card testCard = new Card("123456789012", "12/25", "1234");
        authService.registerCard(testCard);
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void shouldAuthenticateSuccessfullyWithCorrectCredentials() {
        mockUI.setInputs("123456789012", "1234");

        boolean success = authHandler.authenticate();

        assertTrue(success);
        assertEquals("123456789012", authHandler.getAuthenticatedCardNumber());
        assertTrue(mockUI.hasMessage("Inloggning lyckades!"));
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void shouldRetryAfterInvalidCard() {
        // Nu räknas invalid card som attempt, så 2 försök räcker
        mockUI.setInputs(
                "999999999999", "1234",    // Attempt 1 - invalid card
                "123456789012", "1234"     // Attempt 2 - success
        );

        boolean success = authHandler.authenticate();

        assertTrue(success);
        assertTrue(mockUI.hasError("Ogiltigt kortnummer. Försök igen."));
        assertTrue(mockUI.hasMessage("Inloggning lyckades!"));
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void shouldRetryAfterWrongPin() {
        mockUI.setInputs(
                "123456789012", "9999",    // Attempt 1 - wrong PIN
                "123456789012", "1234"     // Attempt 2 - correct PIN
        );

        boolean success = authHandler.authenticate();

        assertTrue(success);
        assertTrue(mockUI.hasError("Felaktig PIN-kod. Försök igen. Försök kvar: 2"));
        assertTrue(mockUI.hasMessage("Inloggning lyckades!"));
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void shouldBlockAfterThreeFailedPinAttempts() {
        // 3 fel PIN-koder blockerar kortet
        mockUI.setInputs(
                "123456789012", "0000",    // Attempt 1 - wrong PIN
                "123456789012", "1111",    // Attempt 2 - wrong PIN
                "123456789012", "2222"     // Attempt 3 - wrong PIN (blocked)
        );

        boolean success = authHandler.authenticate();

        assertFalse(success);
        assertNull(authHandler.getAuthenticatedCardNumber());
        assertTrue(mockUI.hasError("För många felaktiga försök. Kortet är nu blockerat."));
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void shouldFailAfterThreeInvalidCards() {
        // Nu som invalid card räknas som attempt, får vi max 3 försök
        mockUI.setInputs(
                "999999999999", "1234",    // Attempt 1 - invalid card
                "999999999998", "1234",    // Attempt 2 - invalid card
                "999999999997", "1234"     // Attempt 3 - invalid card (max reached)
        );

        boolean success = authHandler.authenticate();

        assertFalse(success);
        assertNull(authHandler.getAuthenticatedCardNumber());
        assertTrue(mockUI.hasError("För många felaktiga försök. Kortet är nu blockerat."));

        // Verifiera att vi fick 3 felmeddelanden
        long invalidCardErrors = mockUI.getErrors().stream()
                .filter(error -> error.contains("Ogiltigt kortnummer"))
                .count();
        assertEquals(3, invalidCardErrors);
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void shouldHandleAlreadyBlockedCard() {
        // Blockera kortet först genom service
        authService.authenticate("123456789012", "0000");
        authService.authenticate("123456789012", "1111");
        authService.authenticate("123456789012", "2222");

        mockUI.setInputs("123456789012", "1234");

        boolean success = authHandler.authenticate();

        assertFalse(success);
        assertTrue(mockUI.hasError("Kortet är blockerat. Kontakta kundtjänst."));
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void shouldHandleMixedErrorsWithinAttemptLimit() {
        // Blandning av fel inom 3 attempts
        mockUI.setInputs(
                "999999999999", "1234",    // Attempt 1 - invalid card
                "123456789012", "9999",    // Attempt 2 - wrong PIN
                "123456789012", "1234"     // Attempt 3 - success
        );

        boolean success = authHandler.authenticate();

        assertTrue(success);
        assertTrue(mockUI.hasError("Ogiltigt kortnummer. Försök igen."));
        assertTrue(mockUI.hasError("Felaktig PIN-kod. Försök igen. Försök kvar: 1"));
        assertTrue(mockUI.hasMessage("Inloggning lyckades!"));
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void shouldFailWithMixedErrorsExceedingLimit() {
        // Blandning av fel som överskrider 3 attempts
        mockUI.setInputs(
                "999999999999", "1234",    // Attempt 1 - invalid card
                "999999999998", "1234",    // Attempt 2 - invalid card
                "123456789012", "9999"     // Attempt 3 - wrong PIN (max reached)
        );

        boolean success = authHandler.authenticate();

        assertFalse(success);
        assertNull(authHandler.getAuthenticatedCardNumber());
        assertTrue(mockUI.hasError("För många felaktiga försök. Kortet är nu blockerat."));
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void shouldPromptForCardNumberAndPin() {
        mockUI.setInputs("123456789012", "1234");

        authHandler.authenticate();

        assertTrue(mockUI.hasMessageContaining("kortnummer (12 siffror)"));
        assertTrue(mockUI.hasMessageContaining("PIN-kod"));
    }
}