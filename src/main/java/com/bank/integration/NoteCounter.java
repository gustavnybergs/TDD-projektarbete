package com.bank.integration;

import java.util.Map;

public interface NoteCounter {
    int countAndVerify(Map<Integer, Integer> sedlar);
}
