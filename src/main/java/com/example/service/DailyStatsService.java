package com.example.service;

import com.example.model.DailyStats;
import com.example.model.User;
import com.example.repository.DailyStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DailyStatsService {
    private final DailyStatsRepository dailyStatsRepository;

    @Autowired
    public DailyStatsService(DailyStatsRepository dailyStatsRepository) {
        this.dailyStatsRepository = dailyStatsRepository;
    }

    public DailyStats getDailyStatsById(Long id) {
        return dailyStatsRepository.findById(id).orElse(null);
    }

    public DailyStats getDailyStatsByUser(User user) {
        return dailyStatsRepository.findByUser(user);
    }

    public DailyStats saveDailyStats(DailyStats dailyStats) {
        return dailyStatsRepository.save(dailyStats);
    }

}
