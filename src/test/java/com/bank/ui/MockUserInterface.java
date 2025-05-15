package com.bank.ui;

import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

/**
 * Mock implementation av UserInterface för tester.
 * Kastar exception när slut på inputs för att bryta infinite loops.
 */
public class MockUserInterface implements UserInterface {
    private final Queue<String> inputQueue = new LinkedList<>();
    private final List<String> messages = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();
    private final Queue<Boolean> confirmationAnswers = new LinkedList<>();
    private int inputCallCount = 0;
    private static final int MAX_INPUT_CALLS = 50; // Säkerhetsventil

    /**
     * Förkonfigurerar input-svar som kommer returneras vid getInput().
     */
    public void setInputs(String... inputs) {
        inputQueue.clear();
        inputCallCount = 0;
        for (String input : inputs) {
            inputQueue.offer(input);
        }
    }

    /**
     * Förkonfigurerar svar för confirmAction().
     */
    public void setConfirmationAnswers(boolean... answers) {
        confirmationAnswers.clear();
        for (boolean answer : answers) {
            confirmationAnswers.offer(answer);
        }
    }

    @Override
    public String getInput(String prompt) {
        inputCallCount++;
        messages.add("INPUT: " + prompt);

        // Säkerhetsventil - förhindra infinite loops
        if (inputCallCount > MAX_INPUT_CALLS) {
            throw new IllegalStateException(
                    "Too many input calls (" + inputCallCount + "). Possible infinite loop detected!"
            );
        }

        String input = inputQueue.poll();

        if (input == null) {
            // VIKTIGT: När det är slut på inputs, kastas exception för att stoppa loopen
            throw new IllegalStateException(
                    "MockUserInterface ran out of inputs after " + inputCallCount +
                            " calls. Last prompt: " + prompt +
                            "\nMake sure to configure enough inputs for your test!"
            );
        }

        return input;
    }

    @Override
    public void showMessage(String message) {
        messages.add(message);
    }

    @Override
    public void showError(String errorMessage) {
        errors.add(errorMessage);
    }

    @Override
    public boolean confirmAction(String message) {
        messages.add("CONFIRM: " + message);
        Boolean answer = confirmationAnswers.poll();
        if (answer == null) {
            throw new IllegalStateException(
                    "MockUserInterface ran out of confirmation answers for: " + message
            );
        }
        return answer;
    }

    @Override
    public String maskSensitiveInput(String input) {
        if (input == null) return null;
        return "*".repeat(input.length());
    }

    // Hjälpmetoder för tester
    public List<String> getMessages() { return new ArrayList<>(messages); }
    public List<String> getErrors() { return new ArrayList<>(errors); }

    public void clearHistory() {
        messages.clear();
        errors.clear();
        inputCallCount = 0;
    }

    // Kontrollmetoder för assert-statements
    public boolean hasMessage(String message) {
        return messages.contains(message);
    }

    public boolean hasError(String error) {
        return errors.contains(error);
    }

    public boolean hasMessageContaining(String substring) {
        return messages.stream().anyMatch(msg -> msg.contains(substring));
    }

    public boolean hasErrorContaining(String substring) {
        return errors.stream().anyMatch(error -> error.contains(substring));
    }

    // Debug-hjälp
    public void printAll() {
        System.out.println("=== MockUserInterface Debug ===");
        System.out.println("Input calls: " + inputCallCount);
        System.out.println("Remaining inputs: " + inputQueue.size());
        System.out.println("=== ALL MESSAGES ===");
        messages.forEach(System.out::println);
        System.out.println("=== ALL ERRORS ===");
        errors.forEach(System.out::println);
    }

    // Status
    public int remainingInputs() {
        return inputQueue.size();
    }

    public int getInputCallCount() {
        return inputCallCount;
    }
}