package com.test.service;

import com.atm.integration.SimuleradSedelräknare;
import com.atm.integration.Transaktionslogg;
import com.atm.model.Konto;
import com.atm.interfaces.Sedelräknare;
import com.atm.service.InsättningsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InsättningsServiceTest {

    private InsättningsService service;
    private Konto konto;

    @BeforeEach
    void setUp() {
        // Använd korrekt implementation
        Sedelräknare räknare = new SimuleradSedelräknare();
        Transaktionslogg logg = new Transaktionslogg();
        service = new InsättningsService(räknare, logg);

        // Skapa ett testkonto
        konto = new Konto("1234", 1000);
    }

    @Test
    void testSättInBekräftadInsättning() {
        Map<Integer, Integer> sedlar = Map.of(100, 2, 200, 1); // 400 kr totalt
        service.sättIn(konto, sedlar, true);

        assertEquals(1400, konto.getSaldo(), "Salot bör ha uppdaterats med 400 kr");
    }

    @Test
    void testSättInEjBekräftadInsättning() {
        Map<Integer, Integer> sedlar = Map.of(500, 1); // 500 kr
        service.sättIn(konto, sedlar, false);

        assertEquals(1000, konto.getSaldo(), "Saldot bör inte förändras vid avbruten insättning");
    }

    @Test
    void testSättInOgiltigValörSkaKastaException() {
        Map<Integer, Integer> ogiltigaSedlar = Map.of(50, 2); // 50 kr är ogiltig

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.sättIn(konto, ogiltigaSedlar, true));

        assertTrue(ex.getMessage().contains("Ogiltig sedelvalör"), "Felmeddelande bör indikera ogiltig valör");
    }
}