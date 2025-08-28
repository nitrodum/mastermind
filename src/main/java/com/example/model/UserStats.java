package com.example.model;

import jakarta.persistence.*;

@Entity
public class UserStats {
    @Id
    @GeneratedValue
    private long id;

    private int gamesPlayed;
    private int gamesWon;
    private int bestScore;
    private int totalScore;
    private int longestWinStreak;
    private int currentWinStreak;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public UserStats(User user) {
        this.gamesPlayed = 0;
        this.gamesWon = 0;
        this.bestScore = 0;
        this.totalScore = 0;
        this.longestWinStreak = 0;
        this.user = user;
        this.currentWinStreak = 0;
    }

    public UserStats() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getLongestWinStreak() {
        return longestWinStreak;
    }

    public void setLongestWinStreak(int longestWinStreak) {
        this.longestWinStreak = longestWinStreak;
    }

    public int getCurrentWinStreak() {
        return currentWinStreak;
    }

    public void setCurrentWinStreak(int currentWinStreak) {
        this.currentWinStreak = currentWinStreak;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getWinRate() {
        return gamesPlayed == 0 ? 0.0 : (double) gamesWon / gamesPlayed * 100;
    }

    public double getAverageScore() {
        return gamesWon == 0 ? 0.0 : (double) totalScore / gamesPlayed;
    }

    public void gameWon(int score) {
        this.gamesPlayed++;
        this.gamesWon++;
        this.totalScore += score;
        if(this.bestScore == 0 || score < this.bestScore) {
            this.bestScore = score;
        }
        this.currentWinStreak++;
        if(this.currentWinStreak > this.longestWinStreak) {
            this.longestWinStreak = this.currentWinStreak;
        }
    }

    public void gameLost(int score) {
        this.gamesPlayed++;
        this.totalScore += score;
        this.currentWinStreak = 0;
    }

}
