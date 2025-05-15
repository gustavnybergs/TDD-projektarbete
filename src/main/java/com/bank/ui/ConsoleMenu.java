package com.bank.ui;

import com.bank.model.Card;
import com.bank.model.Account;
import com.bank.service.account.AccountService;
import com.bank.service.auth.AuthenticationService;
import com.bank.repository.AccountRepository;
import com.bank.repository.CardRepository;
import com.bank.repository.InMemoryAccountRepository;
import com.bank.repository.InMemoryCardRepository;
import com.bank.ui.handlers.AccountHandler;
import com.bank.ui.handlers.AuthenticationHandler;
import com.bank.ui.handlers.TransactionHandler;

/**
 * Huvudklassen för bankomatens användargränssnitt.
 * Denna klass sätter upp systemet, initierar alla nödvändiga komponenter,
 * och koordinerar de olika handlers som hanterar specifika delar av funktionaliteten.
 *
 * Uppdaterad för att använda AccountService för både uttag och insättningar.
 * Uppdaterad för att använda UserInterface abstraction istället av hårdkodad console-interaktion.
 */
public class ConsoleMenu {
    private final UserInterface ui;
    private final AuthenticationHandler authHandler;
    private final AccountHandler accountHandler;
    private final TransactionHandler transactionHandler;

    /**
     * Skapar en ny instans av bankomatens användargränssnitt.
     * Initierar alla nödvändiga services, repositories och handlers.
     */
    public ConsoleMenu() {
        // Initiera UserInterface - nu kan vi enkelt byta till webb/mobile senare
        this.ui = new ConsoleUI();

        // Initiera repositories
        AccountRepository accountRepository = new InMemoryAccountRepository();
        CardRepository cardRepository = new InMemoryCardRepository();

        // Initiera användargränssnittet
        AccountService accountService = new AccountService(accountRepository);
        AuthenticationService authService = new AuthenticationService(cardRepository);

        // Skapa testkonton och kort
        setupTestData(accountRepository, cardRepository);

        // Initiera handlers - alla använder nu UserInterface
        authHandler = new AuthenticationHandler(ui, authService);
        accountHandler = new AccountHandler(ui, accountService);

        // TransactionHandler behöver nu bara AccountHandler (som har AccountService)
        transactionHandler = new TransactionHandler(ui, accountHandler);
    }

    /**
     * Skapar testdata för utvecklings- och demonstrationssyfte.
     * Initierar testkort och testkonton och kopplar dem till varandra.
     *
     * @param accountRepository Repository för kontohantering
     * @param cardRepository Repository för korthantering
     */
    private void setupTestData(AccountRepository accountRepository, CardRepository cardRepository) {
        // Skapa testkort
        Card card1 = new Card("123456789012", "12/25", "1234");
        Card card2 = new Card("098765432109", "06/26", "4321");
        cardRepository.saveCard(card1);
        cardRepository.saveCard(card2);

        // Skapa testkonton
        Account account1 = new Account("1001", "Lönekonto", 500.0);
        Account account2 = new Account("1002", "Sparkonto", 1200.0);
        Account account3 = new Account("2001", "Resekonto", 3000.0);
        accountRepository.saveAccount(account1);
        accountRepository.saveAccount(account2);
        accountRepository.saveAccount(account3);

        // Koppla konton till kort
        accountRepository.linkAccountToCard("1001", "123456789012");
        accountRepository.linkAccountToCard("1002", "123456789012");
        accountRepository.linkAccountToCard("2001", "098765432109");
    }

    /**
     * Startar bankomatgränssnittet.
     * Hanterar autentisering och visar huvudmenyn om autentiseringen lyckas.
     */
    public void start() {
        ui.showMessage("Startar bankomatsystemet...");
        ui.showMessage("Välkommen till bankomaten!");

        // Autentisering hanteras av AuthenticationHandler
        if (!authHandler.authenticate()) {
            ui.showMessage("Avslutar program efter misslyckad inloggning.");
            return;
        }

        // Hämta autentiserat kort för användning i andra handlers
        String cardNumber = authHandler.getAuthenticatedCardNumber();
        accountHandler.setAuthenticatedCardNumber(cardNumber);

        // Huvudmenyn visas endast efter lyckad autentisering
        showMainMenu();
    }

    /**
     * Visar huvudmenyn och hanterar användarens val.
     * Delegerar funktionalitet till respektive handler baserat på användarens val.
     */
    private void showMainMenu() {
        while (true) {
            ui.showMessage("\n--- Bankomat Huvudmeny ---");
            ui.showMessage("1. Sätt in pengar");
            ui.showMessage("2. Ta ut pengar");
            ui.showMessage("3. Visa saldo");
            ui.showMessage("0. Avsluta");

            String choice = ui.getInput("Välj ett alternativ: ");

            switch (choice) {
                case "1" -> transactionHandler.handleDeposit();
                case "2" -> transactionHandler.handleWithdrawal();
                case "3" -> accountHandler.showBalance();
                case "0" -> {
                    ui.showMessage("Avslutar. Hej då!");
                    return;
                }
                default -> ui.showError("Ogiltigt val.");
            }
        }
    }
}