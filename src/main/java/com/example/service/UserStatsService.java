package com.example.service;

import com.example.model.User;
import com.example.model.UserStats;
import com.example.repository.UserStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserStatsService {
    private final UserStatsRepository userStatsRepository;

    @Autowired
    public UserStatsService(UserStatsRepository userStatsRepository) {
        this.userStatsRepository = userStatsRepository;
    }

    public UserStats getUserStatsByUser(User user) {
        return userStatsRepository.findByUser(user);
    }

    public UserStats saveUserStats(UserStats userStats) {
        return userStatsRepository.save(userStats);
    }
}
