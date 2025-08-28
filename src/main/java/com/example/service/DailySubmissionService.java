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
import java.util.Map;

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
        Date gameDate = Date.from(today.atStartOfDay(ZoneOffset.UTC).toInstant());
        return dailySubmissionRepository.findAllByGameDate(gameDate);
    }

    public List<DailySubmission> getDailySubmissionsByDate(Date gameDate) {
        return dailySubmissionRepository.findAllByGameDate(gameDate);
    }

    public DailySubmission saveDailySubmission(DailySubmission dailySubmission) {
        return dailySubmissionRepository.save(dailySubmission);
    }

/**
 * Calculates percentile statistics for a user's performance on a specific daily game.
 * Compares the user's attempt count against all other submissions for the same game date
 * to determine relative performance metrics.
 *
 * @param gameDate the date of the daily game to analyze submissions for
 * @param attempts the number of attempts the current user took to complete the game
 * @return a Map containing performance statistics with the following keys:
 *         <ul>
 *           <li>"totalPlayers" - total number of players who submitted on this date</li>
 *           <li>"betterThan" - number of players who performed worse (took more attempts)</li>
 *           <li>"sameScore" - number of players with identical attempt count</li>
 *           <li>"percentile" - percentile ranking (0-100) where lower is better performance</li>
 *         </ul>
 **/
    public Map<String, Object> calculatePercentileStats(Date gameDate, int attempts) {
        List<DailySubmission> allSubmissions = dailySubmissionRepository.findAllByGameDate(gameDate);

        if (allSubmissions.isEmpty()) {
            return Map.of(
                    "totalPlayers", 1,
                    "betterThan", 0,
                    "sameScore", 1,
                    "percentile", 0
            );
        }

        int totalPlayers = allSubmissions.size();
        int worseOrEqual = (int) allSubmissions.stream()
                .filter(submission -> submission.getAttempts() >= attempts)
                .count();

        double percentile = ((double) (totalPlayers - worseOrEqual) / totalPlayers) * 100;

        if (totalPlayers == 1 || worseOrEqual == totalPlayers) {
            percentile = 0.0;
        }

        int sameScore = (int) allSubmissions.stream()
                .filter(submission -> submission.getAttempts() == attempts)
                .count();

        return Map.of(
                "totalPlayers", totalPlayers,
                "betterThan", worseOrEqual - 1,
                "sameScore", sameScore,
                "percentile", percentile
        );
    }

}
