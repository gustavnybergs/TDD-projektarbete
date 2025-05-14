package com.bank.integration;

import com.bank.util.BankConstants;
import java.util.Map;

public class SimulatedNoteCounter implements NoteCounter {

    // Metod som räknar och verifierar summan av sedlar
    @Override
    public int countAndVerify(Map<Integer, Integer> notes) {
        int sum = 0;
        // Går igenom varje post i Map:en där nyckeln är sedelns valör (t.ex. 100 kr)
        // och värdet är hur många sådana sedlar det finns
        for (Map.Entry<Integer, Integer> entry : notes.entrySet()) {
            int denomination = entry.getKey();   // t.ex. 100, 200, 500
            int count = entry.getValue();        // hur många sedlar av den valören

            // Kontrollera att valören är giltig
            boolean isValidDenomination = false;
            for (int validDenomination : BankConstants.VALID_DENOMINATIONS) {
                if (denomination == validDenomination) {
                    isValidDenomination = true;
                    break;
                }
            }

            // Om inte, kasta ett undantag (fel)
            if (!isValidDenomination) {
                throw new IllegalArgumentException("Ogiltig sedelvalör: " + denomination);
            }

            // Lägg till värdet av sedlarna i totalsumman
            sum += denomination * count;
        }
        // Returnerar den totala summan av sedlarna
        return sum;
    }
}