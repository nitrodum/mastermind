package com.example.service;

import com.example.model.DailyGame;
import com.example.repository.DailyGameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DailyGameService {
    private final DailyGameRepository dailyGameRepository;
    private final RandomOrgService randomOrgService;
    private final Logger logger = LoggerFactory.getLogger(DailyGameService.class);

    @Autowired
    public DailyGameService(DailyGameRepository dailyGameRepository, RandomOrgService randomOrgService) {
        this.dailyGameRepository = dailyGameRepository;
        this.randomOrgService = randomOrgService;
    }

    public DailyGame getDailyGameByDate(Date date) {
        return dailyGameRepository.findById(date).orElse(null);
    }

    public DailyGame saveDailyGame(DailyGame dailyGame) {
        return dailyGameRepository.save(dailyGame);
    }

    /**
     * Gets today's daily game, creating it if it doesn't exist.
     * Uses UTC timezone to ensure consistent daily game availability across time zones.
     *
     * @return the DailyGame for today's date
     */
    public DailyGame getTodaysDailyGame() {
        Date today = getTodayDate();

        Optional<DailyGame> dailyGameOptional = dailyGameRepository.findById(today);

        if (dailyGameOptional.isPresent()) {
            return dailyGameOptional.get();
        }
        DailyGame newDailyGame = createTodaysDailyGame();
        dailyGameRepository.save(newDailyGame);
        return newDailyGame;
    }

    /**
     * Scheduled task that creates a new daily game at midnight UTC.
     * Prevents duplicate creation by checking if today's game already exists.
     * Runs every day at 00:00 UTC.
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    public void createDailyGame() {
        Date today = getTodayDate();

        if (dailyGameRepository.findById(today).isPresent()) {
            return; // Daily game for today already exists
        }
        dailyGameRepository.save(createTodaysDailyGame());
    }

    /**
     * Gets today's date in UTC timezone for consistent daily game timing.
     * Normalizes to start of day (00:00:00) to ensure proper date comparison.
     *
     * @return today's date as a Date object set to midnight UTC
     */
    public Date getTodayDate() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        Date todayDate = Date.from(today.atStartOfDay().toInstant(ZoneOffset.UTC));
        return todayDate;
    }

    private DailyGame createTodaysDailyGame() {
        Date today = getTodayDate();

        List<Integer> code = randomOrgService.getRandomCode();

        String codeString = code.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(""));

        return new DailyGame(today, codeString);
    }
}
