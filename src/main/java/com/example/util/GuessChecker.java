package com.example.util;

import com.example.model.Feedback;

import java.util.ArrayList;
import java.util.List;

public class GuessChecker {

    public static Feedback checkGuess(List<Integer> code, List<Integer> guess) {
        Feedback feedback = new Feedback();
        List<Integer> codeCopy = new ArrayList<>(code);
        List<Integer> guessCopy = new ArrayList<>(guess);

        for (int i = 0; i < guessCopy.size(); i++) {
            if (codeCopy.get(i).equals(guessCopy.get(i))) {
                feedback.setCorrectNumbers(feedback.getCorrectNumbers()+1);
                feedback.setCorrectPositions(feedback.getCorrectPositions()+1);
                codeCopy.set(i, codeCopy.get(i)*-1);
                guessCopy.set(i, null);
            }
        }

        for (Integer integer : guessCopy) {
            if (codeCopy.contains(integer)) {
                feedback.setCorrectNumbers(feedback.getCorrectNumbers() + 1);
                codeCopy.set(codeCopy.indexOf(integer), null);
            }
        }

        return feedback;
    }
}
