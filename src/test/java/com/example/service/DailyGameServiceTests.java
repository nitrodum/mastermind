package com.example.service;

import com.example.model.DailyGame;
import com.example.repository.DailyGameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DailyGameServiceTests {

    private DailyGameRepository dailyGameRepository;
    private RandomOrgService randomOrgService;
    private DailyGameService dailyGameService;

    @BeforeEach
    public void setUp() {
        dailyGameRepository = mock(DailyGameRepository.class);
        randomOrgService = mock(RandomOrgService.class);
        dailyGameService = new DailyGameService(dailyGameRepository, randomOrgService);
    }

    @Test
    public void testGetTodayDate_MidnightUtc() {
        Date expected = Date.from(LocalDate.now(ZoneOffset.UTC).atStartOfDay(ZoneOffset.UTC).toInstant());
        Date actual = dailyGameService.getTodayDate();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetDailyGameByDate_PassThrough() {
        Date date = new Date();
        DailyGame game = new DailyGame(date, "0123");
        when(dailyGameRepository.findById(date)).thenReturn(Optional.of(game));

        DailyGame result = dailyGameService.getDailyGameByDate(date);
        assertSame(game, result);
    }

    @Test
    public void testSaveDailyGame_PassThrough() {
        DailyGame game = new DailyGame(new Date(), "1234");
        when(dailyGameRepository.save(game)).thenReturn(game);

        DailyGame result = dailyGameService.saveDailyGame(game);
        assertSame(game, result);
    }

    @Test
    public void testGetTodaysDailyGame_ReturnsExisting() {
        DailyGame existing = new DailyGame(new Date(), "9876");
        when(dailyGameRepository.findById(any())).thenReturn(Optional.of(existing));

        DailyGame result = dailyGameService.getTodaysDailyGame();

        assertSame(existing, result);
        verify(dailyGameRepository, never()).save(any(DailyGame.class));
    }

    @Test
    public void testGetTodaysDailyGame_CreatesWhenMissing() {
        when(dailyGameRepository.findById(any())).thenReturn(Optional.empty());
        when(randomOrgService.getRandomCode()).thenReturn(List.of(1, 2, 3, 4));

        DailyGame result = dailyGameService.getTodaysDailyGame();

        ArgumentCaptor<DailyGame> captor = ArgumentCaptor.forClass(DailyGame.class);
        verify(dailyGameRepository).save(captor.capture());
        DailyGame saved = captor.getValue();
        assertEquals("1234", saved.getCode());
        assertNotNull(saved.getDate());

        assertEquals("1234", result.getCode());
        assertEquals(saved.getDate(), result.getDate());
    }

    @Test
    public void testCreateDailyGame_Scheduled_SkipsIfExists() {
        when(dailyGameRepository.findById(any())).thenReturn(Optional.of(new DailyGame()));

        dailyGameService.createDailyGame();

        verify(dailyGameRepository, never()).save(any(DailyGame.class));
    }

    @Test
    public void testCreateDailyGame_Scheduled_CreatesIfMissing() {
        when(dailyGameRepository.findById(any())).thenReturn(Optional.empty());
        when(randomOrgService.getRandomCode()).thenReturn(List.of(3, 1, 4, 1));

        dailyGameService.createDailyGame();

        ArgumentCaptor<DailyGame> captor = ArgumentCaptor.forClass(DailyGame.class);
        verify(dailyGameRepository).save(captor.capture());
        assertEquals("3141", captor.getValue().getCode());
        assertNotNull(captor.getValue().getDate());
    }
}

