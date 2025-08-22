package com.example.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private int[] code;
    private int maxAttempts;
    private int attempts;
    private List<Guess> guesses;

    public Game(int[] code, int maxAttempts) {
        this.code = code;
        this.maxAttempts = maxAttempts;
        this.attempts = 0;
        guesses = new ArrayList<>(maxAttempts);
    }

    public Game() {

    }

    public int[] getCode() {
        return code;
    }

    public void setCode(int[] code) {
        this.code = code;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public List<Guess> getGuesses() {
        return this.guesses;
    }

    public void setGuesses(List<Guess> guesses) {
        this.guesses = guesses;
    }
}
