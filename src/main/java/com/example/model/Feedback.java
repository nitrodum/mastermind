package com.example.model;

public class Feedback {
    private int correctNumbers;
    private int correctPositions;

    public int getCorrectNumbers() {
        return correctNumbers;
    }

    public void setCorrectNumbers(int correctNumbers) {
        this.correctNumbers = correctNumbers;
    }

    public int getCorrectPositions() {
        return correctPositions;
    }

    public void setCorrectPositions(int correctPositions) {
        this.correctPositions = correctPositions;
    }

    @Override
    public String toString() {
        return String.format("%d correct numbers in %d correct positions", correctNumbers, correctPositions);
    }
}
