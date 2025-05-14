package com.bank.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testklass för att verifiera funktionaliteten i SimulatedNoteCounter.
 * Den kontrollerar både korrekta summeringar och hantering av ogiltiga valörer.
 */
class SimulatedNoteCounterTest {

    private SimulatedNoteCounter counter;

    @BeforeEach
    void setUp() {
        // Initierar ett nytt objekt innan varje test körs
        counter = new SimulatedNoteCounter();
    }

    /**
     * Testar att summering fungerar korrekt för giltiga sedlar.
     * 2 x 100 + 1 x 200 + 1 x 500 = 900
     */
    @Test
    void countAndVerify_shouldReturnCorrectSum_forValidNotes() {
        Map<Integer, Integer> sedlar = Map.of(
                100, 2,   // 200
                200, 1,   // 200
                500, 1    // 500
        );

        int summa = counter.countAndVerify(sedlar);

        assertEquals(900, summa, "Summan av sedlarna borde vara 900");
    }

    /**
     * Testar att metoden kastar undantag när en ogiltig sedel används.
     * Här testas 50 kr som inte är tillåten.
     */
    @Test
    void countAndVerify_shouldThrowException_forInvalidNote() {
        Map<Integer, Integer> ogiltigaSedlar = Map.of(
                50, 2   // Ogiltig valör
        );

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                counter.countAndVerify(ogiltigaSedlar)
        );

        assertTrue(ex.getMessage().contains("Ogiltig sedelvalör"), "Felmeddelandet bör indikera ogiltig valör");
    }

    /**
     * Testar att metoden fungerar även om inga sedlar ges (summan ska vara 0).
     */
    @Test
    void countAndVerify_shouldReturnZero_whenNoNotesGiven() {
        Map<Integer, Integer> tomt = Map.of();

        int summa = counter.countAndVerify(tomt);

        assertEquals(0, summa, "Summan bör vara 0 när inga sedlar ges");
    }
}