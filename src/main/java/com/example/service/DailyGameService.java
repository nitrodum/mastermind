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

    public DailyGame getTodaysDailyGame() {
        Date today = getTodayDate();
        logger.debug("Fetching daily game for date: {}", today);

        Optional<DailyGame> dailyGameOptional = dailyGameRepository.findById(today);

        if (dailyGameOptional.isPresent()) {
            logger.info("Found existing daily game for {}: code={}", today, dailyGameOptional.get().getCode());
            return dailyGameOptional.get();
        }

        logger.info("No daily game found for {}, creating new one", today);
        DailyGame newDailyGame = createTodaysDailyGame();

        logger.debug("Generated new daily game: date={}, code={}", today, newDailyGame.getCode());

        dailyGameRepository.save(newDailyGame);
        logger.info("Successfully saved new daily game for {}", today);

        return newDailyGame;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    public void createDailyGame() {
        Date today = getTodayDate();

        if (dailyGameRepository.findById(today).isPresent()) {
            return; // Daily game for today already exists
        }
        dailyGameRepository.save(createTodaysDailyGame());
    }

    public Date getTodayDate() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        logger.debug("Today's date calculated as: {}", today);

        Date todayDate = Date.from(today.atStartOfDay().toInstant(ZoneOffset.UTC));
        logger.debug("Converted today's date to Date object: {}", todayDate);

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
