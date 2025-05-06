package com.atm.model;

public class Konto {
    private String kontonummer;
    private int saldo;

    public Konto(String kontonummer, int startsaldo) {
        this.kontonummer = kontonummer;
        this.saldo = startsaldo;
    }

    public void sättIn(int belopp) {
        this.saldo += belopp;
    }

    public void taUt(int belopp) {
        if (belopp <= 0) {
            throw new IllegalArgumentException("Belopp måste vara större än noll");
        }
        if (belopp > saldo) {
            throw new IllegalArgumentException("Otillräckligt saldo");
        }
        saldo -= belopp;
    }

    public int getSaldo() {
        return saldo;
    }

    public String getKontonummer() {
        return kontonummer;
    }
}