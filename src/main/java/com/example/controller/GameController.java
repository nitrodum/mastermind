package com.example.controller;

import com.example.model.Game;
import com.example.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;


@Controller
public class GameController {

    private final GameService gameService;
    private Game game;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/")
    public String index(Model model) {
        game = gameService.createGame(10);

        return "index";
    }

    @PostMapping("/guess")
    public String guess(@ModelAttribute("game") String guess, Model model) {
        return "index";
    }
}
