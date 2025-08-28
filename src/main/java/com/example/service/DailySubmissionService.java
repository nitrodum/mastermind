package com.example.service;

import com.example.model.DailySubmission;
import com.example.model.User;
import com.example.repository.DailySubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@Service
public class DailySubmissionService {
    private final DailySubmissionRepository dailySubmissionRepository;

    @Autowired
    public DailySubmissionService(DailySubmissionRepository dailySubmissionRepository) {
        this.dailySubmissionRepository = dailySubmissionRepository;
    }

    public DailySubmission getDailySubmissionById(Long id) {
        return dailySubmissionRepository.findById(id).orElse(null);
    }

    public List<DailySubmission> getAllDailySubmissions() {
        return dailySubmissionRepository.findAll();
    }

    public DailySubmission findByUserAndGameDate(User user, Date gameDate) {
        return dailySubmissionRepository.findByUserAndGameDate(user, gameDate);
    }

    public boolean hasUserSubmittedToday(User user, Date gameDate) {
        return dailySubmissionRepository.findByUserAndGameDate(user, gameDate) != null;

    }

    public List<DailySubmission> getTodaysDailySubmissions() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        return dailySubmissionRepository.findAll().stream()
                .filter(submission -> {
                    LocalDate submissionDate = submission.getGameDate()
                            .toInstant()
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate();
                            return submissionDate.isEqual(today);
                })
                .toList();
    }

    public DailySubmission saveDailySubmission(DailySubmission dailySubmission) {
        return dailySubmissionRepository.save(dailySubmission);
    }
}
