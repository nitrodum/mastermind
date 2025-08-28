package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;
import java.util.List;

@Entity
public class DailyGame {
    @Id
    private Date date;
    private String code;

    public DailyGame(Date date, String code) {
        this.date = date;
        this.code = code;
    }

    public DailyGame() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Integer> getCodeAsList() {
        return code.chars()
                .map(Character::getNumericValue)
                .boxed()
                .toList();
    }
}
