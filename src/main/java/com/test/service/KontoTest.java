package com.test.service;

import com.atm.model.Konto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KontoTest {

    @Test
    void ska_kunna_ta_ut_pengar_om_tillräckligt_saldo_finns() {
        Konto konto = new Konto("123", 1000);
        konto.taUt(300);
        assertEquals(700, konto.getSaldo());
    }

    @Test
    void ska_inte_kunna_ta_ut_mer_än_saldo() {
        Konto konto = new Konto("123", 500);
        assertThrows(IllegalArgumentException.class, () -> konto.taUt(600));
    }

    @Test
    void ska_inte_kunna_ta_ut_negativt_belopp() {
        Konto konto = new Konto("123", 500);
        assertThrows(IllegalArgumentException.class, () -> konto.taUt(-100));
    }

    @Test
    void ska_inte_kunna_ta_ut_noll_kronor() {
        Konto konto = new Konto("123", 500);
        assertThrows(IllegalArgumentException.class, () -> konto.taUt(0));
    }
    @Test
    void ska_kunna_ta_ut_exakt_helt_saldo() {
        Konto konto = new Konto("123", 500);
        konto.taUt(500);
        assertEquals(0, konto.getSaldo());
    }
}