package com.example.service;

import com.example.model.DailySubmission;
import com.example.model.User;
import com.example.repository.DailySubmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DailySubmissionServiceTests {

    private DailySubmissionRepository dailySubmissionRepository;
    private DailySubmissionService dailySubmissionService;

    @BeforeEach
    public void setUp() {
        dailySubmissionRepository = mock(DailySubmissionRepository.class);
        dailySubmissionService = new DailySubmissionService(dailySubmissionRepository);
    }

    @Test
    public void testGetAllDailySubmissions() {
        List<DailySubmission> submissions = Arrays.asList(new DailySubmission(), new DailySubmission());
        when(dailySubmissionRepository.findAll()).thenReturn(submissions);

        List<DailySubmission> result = dailySubmissionService.getAllDailySubmissions();
        assertEquals(2, result.size());
        assertSame(submissions, result);
    }

    @Test
    public void testFindByUserAndGameDate() {
        User user = new User();
        Date date = new Date();
        DailySubmission ds = new DailySubmission();
        when(dailySubmissionRepository.findByUserAndGameDate(user, date)).thenReturn(ds);

        DailySubmission result = dailySubmissionService.findByUserAndGameDate(user, date);
        assertSame(ds, result);
    }

    @Test
    public void testHasUserSubmittedToday_True() {
        User user = new User();
        Date date = new Date();
        when(dailySubmissionRepository.findByUserAndGameDate(user, date)).thenReturn(new DailySubmission());

        assertTrue(dailySubmissionService.hasUserSubmittedToday(user, date));
    }

    @Test
    public void testHasUserSubmittedToday_False() {
        User user = new User();
        Date date = new Date();
        when(dailySubmissionRepository.findByUserAndGameDate(user, date)).thenReturn(null);

        assertFalse(dailySubmissionService.hasUserSubmittedToday(user, date));
    }

    @Test
    public void testGetTodaysDailySubmissions_UsesUtcMidnight() {
        LocalDate todayUtc = LocalDate.now(ZoneOffset.UTC);
        Date expectedDate = Date.from(todayUtc.atStartOfDay(ZoneOffset.UTC).toInstant());

        List<DailySubmission> submissions = Collections.singletonList(new DailySubmission());
        when(dailySubmissionRepository.findAllByGameDate(any())).thenReturn(submissions);

        List<DailySubmission> result = dailySubmissionService.getTodaysDailySubmissions();

        assertSame(submissions, result);

        ArgumentCaptor<Date> dateCaptor = ArgumentCaptor.forClass(Date.class);
        verify(dailySubmissionRepository, times(1)).findAllByGameDate(dateCaptor.capture());
        assertEquals(expectedDate, dateCaptor.getValue());
    }

    @Test
    public void testGetDailySubmissionsByDate_PassThrough() {
        Date someDate = new Date();
        List<DailySubmission> submissions = Arrays.asList(new DailySubmission(), new DailySubmission());
        when(dailySubmissionRepository.findAllByGameDate(someDate)).thenReturn(submissions);

        List<DailySubmission> result = dailySubmissionService.getDailySubmissionsByDate(someDate);
        assertSame(submissions, result);
    }

    @Test
    public void testSaveDailySubmission_PassThrough() {
        DailySubmission ds = new DailySubmission();
        when(dailySubmissionRepository.save(ds)).thenReturn(ds);

        DailySubmission result = dailySubmissionService.saveDailySubmission(ds);
        assertSame(ds, result);
    }

    @Test
    public void testCalculatePercentileStats_NoSubmissions() {
        Date date = new Date();
        when(dailySubmissionRepository.findAllByGameDate(date)).thenReturn(Collections.emptyList());

        Map<String, Object> stats = dailySubmissionService.calculatePercentileStats(date, 4);

        assertEquals(1, stats.get("totalPlayers"));
        assertEquals(0, stats.get("betterThan"));
        assertEquals(1, stats.get("sameScore"));
        assertEquals(0.0, ((Number) stats.get("percentile")).doubleValue(), 0.00001);
    }

    @Test
    public void testCalculatePercentileStats_TypicalCase() {
        Date date = new Date();
        List<DailySubmission> subs = Arrays.asList(
                submissionWithAttempts(2),
                submissionWithAttempts(4),
                submissionWithAttempts(6),
                submissionWithAttempts(8)
        );
        when(dailySubmissionRepository.findAllByGameDate(date)).thenReturn(subs);

        Map<String, Object> stats = dailySubmissionService.calculatePercentileStats(date, 4);

        assertEquals(4, stats.get("totalPlayers"));
        assertEquals(2, stats.get("betterThan"));
        assertEquals(1, stats.get("sameScore"));
        assertEquals(25.0, ((Number) stats.get("percentile")).doubleValue(), 0.00001);
    }

    @Test
    public void testCalculatePercentileStats_BestCaseAllWorse() {
        Date date = new Date();
        List<DailySubmission> subs = Arrays.asList(
                submissionWithAttempts(5),
                submissionWithAttempts(6),
                submissionWithAttempts(7)
        );
        when(dailySubmissionRepository.findAllByGameDate(date)).thenReturn(subs);

        Map<String, Object> stats = dailySubmissionService.calculatePercentileStats(date, 3);

        assertEquals(3, stats.get("totalPlayers"));
        assertEquals(2, stats.get("betterThan"));
        assertEquals(0, stats.get("sameScore"));
        assertEquals(0.0, ((Number) stats.get("percentile")).doubleValue(), 0.00001);
    }

    @Test
    public void testCalculatePercentileStats_AllSameScore() {
        Date date = new Date();
        List<DailySubmission> subs = Arrays.asList(
                submissionWithAttempts(4),
                submissionWithAttempts(4),
                submissionWithAttempts(4)
        );
        when(dailySubmissionRepository.findAllByGameDate(date)).thenReturn(subs);

        Map<String, Object> stats = dailySubmissionService.calculatePercentileStats(date, 4);

        assertEquals(3, stats.get("totalPlayers"));
        assertEquals(2, stats.get("betterThan"));
        assertEquals(3, stats.get("sameScore"));
        assertEquals(0.0, ((Number) stats.get("percentile")).doubleValue(), 0.00001);
    }

    private static DailySubmission submissionWithAttempts(int attempts) {
        DailySubmission ds = new DailySubmission();
        ds.setAttempts(attempts);
        return ds;
    }
}
