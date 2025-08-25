package com.example.util;

import com.example.model.Feedback;

import java.util.ArrayList;
import java.util.List;

public class GuessChecker {


    /**
     * Compares the user's guess to the secret code and returns feedback.
     * <p>
     * The feedback consists of two values:
     * <ul>
     *   <li>correctNumbers: The total number of digits in the guess that are present in the code (regardless of position).</li>
     *   <li>correctPositions: The number of digits that are both present and in the correct position.</li>
     * </ul>
     * <p>
     * The method first checks for correct positions, then for correct numbers (excluding already matched positions).
     *
     * @param code  the secret code as a list of integers
     * @param guess the user's guess as a list of integers
     * @return a Feedback object containing the number of correct numbers and correct positions
     */
    public static Feedback checkGuess(List<Integer> code, List<Integer> guess) {
        Feedback feedback = new Feedback();
        List<Integer> codeCopy = new ArrayList<>(code);
        List<Integer> guessCopy = new ArrayList<>(guess);

        for (int i = 0; i < guessCopy.size(); i++) {
            if (codeCopy.get(i).equals(guessCopy.get(i))) {
                feedback.setCorrectNumbers(feedback.getCorrectNumbers() + 1);
                feedback.setCorrectPositions(feedback.getCorrectPositions() + 1);
                codeCopy.set(i, null);
                guessCopy.set(i, null);
            }
        }

        for (Integer integer : guessCopy) {
            if (integer != null && codeCopy.contains(integer)) {
                feedback.setCorrectNumbers(feedback.getCorrectNumbers() + 1);
                codeCopy.set(codeCopy.indexOf(integer), null);
            }
        }

        return feedback;
    }
}
