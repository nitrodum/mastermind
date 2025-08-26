package com.example.controller;

import com.example.model.User;
import com.example.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stats")
public class StatsController {
    private final UserService userService;

    @Autowired
    public StatsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String statsPage(HttpSession session, Model model) {
        User  user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth";
        }

        if (user.getUserStats() != null) {
            session.setAttribute("userStats", user.getUserStats());
        }

        model.addAttribute("username", user.getUsername());

        return "stats";
    }

}
