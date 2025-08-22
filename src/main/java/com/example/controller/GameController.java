package com.example.controller;

import com.example.model.Game;
import com.example.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String guess(@RequestParam("guess1") int guess1,
                        @RequestParam("guess2") int guess2,
                        @RequestParam("guess3") int guess3,
                        @RequestParam("guess4") int guess4,
                        Model model) {

        int[] guessArray = new int[]{guess1, guess2, guess3, guess4};

        String guesses = Arrays.toString(guessArray);

        model.addAttribute("Guesses", guesses);
        return "index";
    }
}
