package com.example.controller;

import com.example.model.DailySubmission;
import com.example.model.DailySubmissionRequest;
import com.example.model.User;
import com.example.service.DailySubmissionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/daily-submission")
public class DailySubmissionController {
    private final DailySubmissionService dailySubmissionService;

    @Autowired
    public DailySubmissionController(DailySubmissionService dailySubmissionService) {
        this.dailySubmissionService = dailySubmissionService;
    }

    @GetMapping("/today")
    public List<DailySubmission> getTodaysDailySubmissions() {
        return dailySubmissionService.getTodaysDailySubmissions();
    }

    @PostMapping("")
    public DailySubmission createDailySubmission(@RequestBody DailySubmissionRequest request, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            throw new IllegalStateException("User not logged in");
        }

        DailySubmission dailySubmission = new DailySubmission(
                request.getAttempts(),
                new Date(),
                request.getGameDate(),
                user
        );



        return dailySubmissionService.saveDailySubmission(dailySubmission);
    }
}
