package com.example.repository;

import com.example.model.DailyStats;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyStatsRepository extends JpaRepository<DailyStats, Long> {
    DailyStats findByUser(User user);
}
