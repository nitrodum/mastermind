package com.example.model;

import jakarta.persistence.*;

@Entity
public class DailyStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int dailyGamesPlayed;
    private int dailyGamesWon;
    private int dailyBestScore;
    private int dailyTotalScore;
    private int dailyLongestWinStreak;
    private int dailyCurrentWinStreak;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public DailyStats(User user) {
        this.dailyGamesPlayed = 0;
        this.dailyGamesWon = 0;
        this.dailyBestScore = 0;
        this.dailyTotalScore = 0;
        this.dailyLongestWinStreak = 0;
        this.dailyCurrentWinStreak = 0;
    }

    public DailyStats() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDailyGamesPlayed() {
        return dailyGamesPlayed;
    }

    public void setDailyGamesPlayed(int dailyGamesPlayed) {
        this.dailyGamesPlayed = dailyGamesPlayed;
    }

    public int getDailyGamesWon() {
        return dailyGamesWon;
    }

    public void setDailyGamesWon(int dailyGamesWon) {
        this.dailyGamesWon = dailyGamesWon;
    }

    public int getDailyBestScore() {
        return dailyBestScore;
    }

    public void setDailyBestScore(int dailyBestScore) {
        this.dailyBestScore = dailyBestScore;
    }

    public int getDailyTotalScore() {
        return dailyTotalScore;
    }

    public void setDailyTotalScore(int dailyTotalScore) {
        this.dailyTotalScore = dailyTotalScore;
    }

    public int getDailyLongestWinStreak() {
        return dailyLongestWinStreak;
    }

    public void setDailyLongestWinStreak(int dailyLongestWinStreak) {
        this.dailyLongestWinStreak = dailyLongestWinStreak;
    }

    public int getDailyCurrentWinStreak() {
        return dailyCurrentWinStreak;
    }

    public void setDailyCurrentWinStreak(int dailyCurrentWinStreak) {
        this.dailyCurrentWinStreak = dailyCurrentWinStreak;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getWinRate() {
        return dailyGamesPlayed == 0 ? 0.0 : (double) dailyGamesWon / dailyGamesPlayed * 100;
    }

    public double getAverageScore() {
        return dailyGamesWon == 0 ? 0.0 : (double) dailyTotalScore / dailyGamesPlayed;
    }

    public void gameWon(int score) {
        this.dailyGamesPlayed++;
        this.dailyGamesWon++;
        this.dailyTotalScore += score;
        if(this.dailyBestScore == 0 || score < this.dailyBestScore) {
            this.dailyBestScore = score;
        }
        this.dailyCurrentWinStreak++;
        if(this.dailyCurrentWinStreak > this.dailyLongestWinStreak) {
            this.dailyLongestWinStreak = this.dailyCurrentWinStreak;
        }
    }

    public void gameLost(int score) {
        this.dailyGamesPlayed++;
        this.dailyCurrentWinStreak = 0;
        this.dailyTotalScore += score;
    }
}
