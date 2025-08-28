package com.example.repository;

import com.example.model.User;
import com.example.model.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatsRepository extends JpaRepository<UserStats, Long> {
    UserStats findByUser(User user);
}
