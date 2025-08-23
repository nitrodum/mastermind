package com.example.controller;

import com.example.model.Feedback;
import com.example.model.Game;
import com.example.model.Guess;
import com.example.service.GameService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        Game game = (Game) session.getAttribute("game");

        if (game == null) {
            game = gameService.createGame(10);
            session.setAttribute("game", game);
        }

        model.addAttribute("guesses", game.getGuesses());

        return "index";
    }

    @PostMapping("/guess")
    public String guess(@RequestParam("guess1") int guess1,
                        @RequestParam("guess2") int guess2,
                        @RequestParam("guess3") int guess3,
                        @RequestParam("guess4") int guess4,
                        HttpSession session,
                        Model model) {
        Game game = (Game) session.getAttribute("game");

        if (game == null) {
            game = gameService.createGame(10);
            session.setAttribute("game", game);
        }


        List<Integer> guessList = List.of(guess1, guess2, guess3, guess4);

        Guess guess = new Guess(guessList, new Feedback());

        game.addGuess(guess);

        return "redirect:/";
    }

    @PostMapping("/reset")
    public String reset(HttpSession session) {
        session.removeAttribute("game");
        return "redirect:/";
    }
}
