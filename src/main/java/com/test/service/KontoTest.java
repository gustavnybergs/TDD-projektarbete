package com.test.service;

import com.atm.model.Konto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KontoTest {

    // Testa insättning
    @Test
    void testSaldoUppdaterasVidInsättning() {
        Konto konto = new Konto("7777", 100);
        konto.sättIn(300);
        assertEquals(400, konto.getSaldo());
    }

    // Testa uttag
    @Test
    void testSaldoUppdaterasVidUttag() {
        Konto konto = new Konto("7777", 500);
        konto.taUt(200);
        assertEquals(300, konto.getSaldo());
    }

    // Testa att inte kunna ta ut mer än saldo
    @Test
    void testFörsökTaUtMerÄnSaldoGerException() {
        Konto konto = new Konto("7777", 100);
        assertThrows(IllegalArgumentException.class, () -> konto.taUt(200));
    }

    // Testa att saldot uppdateras när uttaget är möjligt
    @Test
    void ska_kunna_ta_ut_pengar_om_tillräckligt_saldo_finns() {
        Konto konto = new Konto("123", 1000);
        konto.taUt(300);
        assertEquals(700, konto.getSaldo());
    }

    // Testa ogiltiga uttag (mer än saldo)
    @Test
    void ska_inte_kunna_ta_ut_mer_än_saldo() {
        Konto konto = new Konto("123", 500);
        assertThrows(IllegalArgumentException.class, () -> konto.taUt(600));
    }

    // Testa att inte ta ut negativa belopp
    @Test
    void ska_inte_kunna_ta_ut_negativt_belopp() {
        Konto konto = new Konto("123", 500);
        assertThrows(IllegalArgumentException.class, () -> konto.taUt(-100));
    }

    // Testa att inte ta ut noll kronor
    @Test
    void ska_inte_kunna_ta_ut_noll_kronor() {
        Konto konto = new Konto("123", 500);
        assertThrows(IllegalArgumentException.class, () -> konto.taUt(0));
    }

    // Testa att ta ut exakt saldo
    @Test
    void ska_kunna_ta_ut_exakt_helt_saldo() {
        Konto konto = new Konto("123", 500);
        konto.taUt(500);
        assertEquals(0, konto.getSaldo());
    }
}