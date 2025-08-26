package com.example.controller;

import com.example.model.User;
import com.example.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String authPage(@RequestParam(required = false) String mode, Model model) {
        model.addAttribute("isRegisterMode", "register".equals(mode));
        return "auth";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, Model model) {
        try {
            User user = userService.registerUser(username, password);
            model.addAttribute("message", "Registration successful for user: " + user.getUsername());
            model.addAttribute("messageType", "success");
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", e.getMessage());
            model.addAttribute("messageType", "error");
            model.addAttribute("isRegisterMode", true);
            return "auth";
        }
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        Optional<User> user = userService.loginUser(username, password);

        if(user.isPresent()) {
            session.setAttribute("user", user.get());
            return "redirect:/";
        } else {
            model.addAttribute("message", "Invalid username or password");
            model.addAttribute("messageType", "error");
            model.addAttribute("isRegisterMode", false);
            return "auth";
        }
    }

    @PostMapping("/logout")
    public String logoutUser(HttpSession session) {
        session.removeAttribute("user");
        session.removeAttribute("game");
        return "redirect:/";
    }
}
