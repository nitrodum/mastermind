package com.example.model;

import java.util.Arrays;
import java.util.List;

public class Guess {
    private List<Integer> guesses;
    private Feedback feedback;

    public Guess(List<Integer> guesses, Feedback feedback) {
        this.guesses = guesses;

    }

    public List<Integer> getGuesses() {
        return guesses;
    }

    public void setGuesses(List<Integer> guesses) {
        this.guesses = guesses;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }
}
