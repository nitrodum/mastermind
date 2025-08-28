package com.example.controller;

import com.example.model.DailyGame;
import com.example.service.DailyGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/daily-game")
public class DailyGameController {
    private final DailyGameService dailyGameService;

    @Autowired
    public DailyGameController(DailyGameService dailyGameService) {
        this.dailyGameService = dailyGameService;
    }

    @GetMapping
    public DailyGame getDailyGame() {
        return dailyGameService.getTodaysDailyGame();
    }


}
