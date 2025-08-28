package com.example.controller;

import com.example.model.DailyStats;
import com.example.model.User;
import com.example.model.UserStats;
import com.example.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/stats")
public class StatsController {
    private final UserService userService;

    @Autowired
    public StatsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String statsPage(@RequestParam(defaultValue = "infinite") String mode, HttpSession session, Model model) {
        User  user = (User) session.getAttribute("user");
        model.addAttribute("username", user.getUsername());
        model.addAttribute("statsMode", mode);

        if (user == null) {
            return "redirect:/auth";
        }

        if (mode.equals("daily")) {
            DailyStats dailyStats = user.getDailyStats();

            if (user.getDailyStats() == null) {
               dailyStats = new DailyStats(user);
            }
            model.addAttribute("currentStats", dailyStats);
        } else {

            UserStats userStats = user.getUserStats();
            if (userStats == null) {
                userStats = new UserStats(user);
            }
            model.addAttribute("currentStats", userStats);
            }

        if (user.getUserStats() != null) {
            session.setAttribute("userStats", user.getUserStats());
        }
        return "stats";
    }

}
