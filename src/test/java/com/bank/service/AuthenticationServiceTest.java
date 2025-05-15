package com.bank.service;
// Testar affärslogik relaterad till konton

import com.bank.model.Card;
import com.bank.repository.CardRepository;
import com.bank.repository.InMemoryCardRepository;
import com.bank.service.auth.AuthenticationResult;
import com.bank.service.auth.AuthenticationService;
import com.bank.ui.ConsoleUI;
import com.bank.ui.UserInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;



public class AuthenticationServiceTest {

    private Card card;
    private AuthenticationService authService;
    private UserInterface userInterface = new ConsoleUI();
    private CardRepository cardRepository = new InMemoryCardRepository();

    @BeforeEach
    public void setUp() {
        // Förbereder testdata innan varje test
        System.out.println("setUp is running!"); // För att bekräfta att metoden körs
        CardRepository cardRepository = new InMemoryCardRepository();
        authService = new AuthenticationService(cardRepository);
        userInterface = new ConsoleUI(); // Mock eller verklig implementation
    }

    @Test
    public void shouldAcceptValidCardNumber() {
        // Arrange
        String validCardNumber = "123456789012";

        // Act
        boolean isAccepted = authService.validateCardNumber(validCardNumber);

        // Assert
        assertTrue(isAccepted, "Giltigt kortnummer ska accepteras");
    }

    @Test
    public void shouldRejectInvalidCardNumber() {
        // Arrange
        String invalidCardNumber = "123456";

        // Act
        boolean isAccepted = authService.validateCardNumber(invalidCardNumber);

        // Assert
        assertFalse(isAccepted, "Ogiltigt kortnummer ska avvisas");
    }

    @Test
    public void shouldAuthenticateWithCorrectPin() {
        // Arrange
        String cardNumber = "123456789012";
        String correctPin = "1234";
        card = new Card(cardNumber,"0101", correctPin);
        authService.registerCard(card);

        // Act
        AuthenticationResult result = authService.authenticate(cardNumber, correctPin);
        // Assert
        assertEquals(AuthenticationResult.SUCCESS, result,
                "Användaren ska autentiseras med rätt PIN-kod");    }

    @Test
    public void shouldFailAuthenticationWithIncorrectPin() {
        // Arrange
        String cardNumber = "123456789012";
        String incorrectPin = "5678";
        String correctPin = "1234";
        card = new Card(cardNumber, "0101", correctPin);
        authService.registerCard(card);

        // Act
        AuthenticationResult result = authService.authenticate(cardNumber, incorrectPin);

        // Assert
        assertEquals(AuthenticationResult.WRONG_PIN, result, "Autentisering ska misslyckas med fel PIN-kod");
    }

    @Test
    public void shouldBlockCardAfterThreeFailedAttempts() {
        // Arrange
        String cardNumber = "123456789012";
        String incorrectPin = "5678";
        String correctPin = "1234";
        card = new Card(cardNumber, "0101", correctPin);
        authService.registerCard(card);

        // Act
        authService.authenticate(cardNumber, incorrectPin); // Första försöket
        authService.authenticate(cardNumber, incorrectPin); // Andra försöket
        authService.authenticate(cardNumber, incorrectPin); // Tredje försöket

        // Assert
        assertEquals(AuthenticationResult.CARD_BLOCKED,
                authService.authenticate(cardNumber, correctPin),
                "Även korrekt PIN ska misslyckas efter att kortet blockerats");
    }

    @Test
    public void shouldMaskPinCodeInputInUserInterface() {
        // Arrange
        String pin = "1234";

        // Act
        String displayedPin = userInterface.maskSensitiveInput(pin);

        // Assert
        assertEquals("****", displayedPin, "PIN-koden ska visas som asterisker");
        assertNotEquals(pin, displayedPin, "PIN-koden ska inte visas i klartext");
    }

    @Test
    public void shouldGrantAccessToBankServicesAfterSuccessfulAuthentication() {
        // Arrange
        String cardNumber = "123456789012";
        String correctPin = "1234";
        card = new Card(cardNumber, "0101", correctPin);
        authService.registerCard(card);

        // Act
        authService.authenticate(cardNumber, correctPin);
        boolean hasAccess = authService.hasAccessToBankServices(cardNumber);

        // Assert
        assertTrue(hasAccess, "Användaren ska ha tillgång till banktjänster efter framgångsrik autentisering");
    }

}
