package com.example.util;

import com.example.model.Feedback;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GuessCheckerTests {

    @Test
    public void testCheckGuessSuccess() {
        List<Integer> code = List.of(1, 2, 3, 4);
        List<Integer> guess = List.of(1, 2, 3, 4);

        Feedback feedback = GuessChecker.checkGuess(code, guess);

        assert feedback.getCorrectNumbers() == 4;
        assert feedback.getCorrectPositions() == 4;
    }

    @Test
    public void testCheckGuessPartialSuccess() {
        List<Integer> code = List.of(1, 2, 3, 4);
        List<Integer> guess = List.of(1, 3, 4, 5);

        Feedback feedback = GuessChecker.checkGuess(code, guess);

        assert feedback.getCorrectNumbers() == 3;
        assert feedback.getCorrectPositions() == 1;
    }

    @Test
    public void testCheckGuessNoSuccess() {
        List<Integer> code = List.of(1, 2, 3, 4);
        List<Integer> guess = List.of(5, 6, 7, 0);

        Feedback feedback = GuessChecker.checkGuess(code, guess);

        assert feedback.getCorrectNumbers() == 0;
        assert feedback.getCorrectPositions() == 0;
    }

    @Test
    public void testCheckGuessWithDuplicatesInCode() {
        List<Integer> code = List.of(1, 1, 2, 3);
        List<Integer> guess = List.of(1, 2, 1, 4);

        Feedback feedback = GuessChecker.checkGuess(code, guess);

        assert feedback.getCorrectNumbers() == 3;
        assert feedback.getCorrectPositions() == 1;
    }

    @Test
    public void testCheckGuessWithDuplicatesInGuess() {
        List<Integer> code = List.of(1, 2, 3, 4);
        List<Integer> guess = List.of(1, 1, 1, 1);

        Feedback feedback = GuessChecker.checkGuess(code, guess);

        assert feedback.getCorrectNumbers() == 1;
        assert feedback.getCorrectPositions() == 1;
    }

}
