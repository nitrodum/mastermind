package com.example.repository;

import com.example.model.DailySubmission;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface DailySubmissionRepository extends JpaRepository<DailySubmission, Long> {
    DailySubmission findByUserAndGameDate(User user, Date gameDate);
    List<DailySubmission> findAllByGameDate(Date gameDate);
}
