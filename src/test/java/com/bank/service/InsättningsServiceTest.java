package com.bank.service;

import com.bank.integration.SimuleradSedelräknare;
import com.bank.integration.Transaktionslogg;
import com.bank.model.Account;
import com.bank.integration.Sedelräknare;
import com.bank.service.InsättningsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class InsättningsServiceTest {

    private InsättningsService service;
    private Account account;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        // Skapa en mock av AccountService
        accountService = new TestAccountService();

        // Använd korrekt implementation
        Sedelräknare räknare = new SimuleradSedelräknare();
        Transaktionslogg logg = new Transaktionslogg();
        service = new InsättningsService(räknare, logg, accountService);

        // Skapa ett testkonto - Account är immutable så vi skapar det via AccountService
        account = new Account("1234", "Testkonto", 1000.0);
        // Typkonvertering för att komma åt metoden direkt i underklassen
        ((TestAccountService)accountService).testAccounts.put(account.getAccountNumber(), account);    }

    @Test
    void testSättInBekräftadInsättning() {
        Map<Integer, Integer> sedlar = Map.of(100, 2, 200, 1); // 400 kr totalt
        service.sättIn("1234", sedlar, true);

        // Hämta det uppdaterade kontot och kontrollera saldot
        Account updatedAccount = accountService.getAccount("1234");
        assertEquals(1400.0, updatedAccount.getBalance(), "Salot bör ha uppdaterats med 400 kr");
    }

    @Test
    void testSättInEjBekräftadInsättning() {
        Map<Integer, Integer> sedlar = Map.of(500, 1); // 500 kr

        // Anropa sättIn med kontonummer istället för konto-objekt
        service.sättIn("1234", sedlar, false);

        // Hämta kontot och verifiera att saldot är oförändrat
        Account unchangedAccount = accountService.getAccount("1234");
        assertEquals(1000.0, unchangedAccount.getBalance(), "Saldot bör inte förändras vid avbruten insättning");
    }

    @Test
    void testSättInOgiltigValörSkaKastaException() {
        Map<Integer, Integer> ogiltigaSedlar = Map.of(50, 2); // 50 kr är ogiltig

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.sättIn("1234", ogiltigaSedlar, true));

        assertTrue(ex.getMessage().contains("Ogiltig sedelvalör"), "Felmeddelande bör indikera ogiltig valör");
    }


    // Inre klass som ärver från AccountService
    private class TestAccountService extends AccountService {
        private Map<String, Account> testAccounts = new HashMap<>();

        public TestAccountService() {
            super(null);
        }

        @Override
        public Account getAccount(String accountNumber) {
            return testAccounts.get(accountNumber);
        }

        @Override
        public Account updatedBalance(String accountNumber, double newBalance) {
            Account oldAccount = testAccounts.get(accountNumber);
            Account newAccount = new Account(
                    oldAccount.getAccountNumber(),
                    oldAccount.getAccountName(),
                    newBalance
            );
            testAccounts.put(accountNumber, newAccount);
            return newAccount;
        }
    }


}