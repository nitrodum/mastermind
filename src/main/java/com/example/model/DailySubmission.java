package com.example.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "gameDate"}))
public class DailySubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int attempts;
    private Date submittedAt;
    private Date gameDate;

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private User user;

    public DailySubmission(int attempts, Date submittedAt, Date gameDate, User user) {
        this.attempts = attempts;
        this.submittedAt = submittedAt;
        this.gameDate = gameDate;
        this.user = user;
    }

    public DailySubmission() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public Date getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Date submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Date getGameDate() {
        return gameDate;
    }

    public void setGameDate(Date gameDate) {
        this.gameDate = gameDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
