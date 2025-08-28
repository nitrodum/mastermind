package com.example.model;

import java.util.Date;

public class DailySubmissionRequest {
    private int attempts;
    private Date gameDate;

    // Constructors
    public DailySubmissionRequest() {}

    public DailySubmissionRequest(int attempts, Date gameDate) {
        this.attempts = attempts;
        this.gameDate = gameDate;
    }

    // Getters and setters
    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public Date getGameDate() {
        return gameDate;
    }

    public void setGameDate(Date gameDate) {
        this.gameDate = gameDate;
    }
}
