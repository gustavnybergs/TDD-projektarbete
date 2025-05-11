package com.bank.integration;

import java.util.Map;

public class SimuleradSedelräknare implements Sedelräknare {

    public int räknaOchVerifiera(Map<Integer, Integer> sedlar) {
        int summa = 0;
        for (Map.Entry<Integer, Integer> entry : sedlar.entrySet()) {
            int valör = entry.getKey();
            int antal = entry.getValue();

            if (valör != 100 && valör != 200 && valör != 500) {
                throw new IllegalArgumentException("Ogiltig sedelvalör: " + valör);
            }

            summa += valör * antal;
        }
        return summa;
    }
}
