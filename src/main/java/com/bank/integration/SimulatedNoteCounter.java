package com.bank.integration;

import com.bank.util.BankConstants;
import java.util.Map;

public class SimulatedNoteCounter implements NoteCounter {

    @Override
    public int countAndVerify(Map<Integer, Integer> notes) {
        int sum = 0;
        for (Map.Entry<Integer, Integer> entry : notes.entrySet()) {
            int denomination = entry.getKey();
            int count = entry.getValue();

            // Använd konstant istället för hardcoded values
            boolean isValidDenomination = false;
            for (int validDenomination : BankConstants.VALID_DENOMINATIONS) {
                if (denomination == validDenomination) {
                    isValidDenomination = true;
                    break;
                }
            }

            if (!isValidDenomination) {
                throw new IllegalArgumentException("Invalid denomination: " + denomination);
            }

            sum += denomination * count;
        }
        return sum;
    }
}