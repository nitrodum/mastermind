package com.example.controller;

import com.example.model.Feedback;
import com.example.model.Game;
import com.example.model.Guess;
import com.example.service.GameService;
import com.example.util.GuessChecker;
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

    /**
     * Handles the main game page.
     * Initializes a new game if one does not exist in the session.
     * Adds game status and details to the model for rendering.
     *
     * @param session the HTTP session to store and retrieve the game
     * @param model the model to pass attributes to the view
     * @return the name of the view to render
     */
    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        Game game = (Game) session.getAttribute("game");

        if (game == null) {
            game = gameService.createGame(10);
            session.setAttribute("game", game);
        }

        if (!game.getGuesses().isEmpty()) {
            Guess lastGuess = game.getGuesses().get(game.getGuesses().size() - 1);

            if (lastGuess.getFeedback().getCorrectPositions() == 4 && lastGuess.getFeedback().getCorrectNumbers() == 4) {
                model.addAttribute("status", "You Win! :)");
            }

            if (game.getAttempts() >= game.getMaxAttempts()) {
                model.addAttribute("status", "You Lose! :(");
            }
        }

        model.addAttribute("maxAttempts", game.getMaxAttempts());
        model.addAttribute("attempts", game.getAttempts());
        model.addAttribute("code", game.getCode());
        model.addAttribute("guesses", game.getGuesses());

        return "index";
    }

    /**
     * Handles a guess submission from the user.
     * Retrieves the current game from the session, checks the user's guess,
     * creates a new Guess object with feedback, and adds it to the game.
     * Redirects back to the main game page.
     *
     * @param guess1 the first number of the guess
     * @param guess2 the second number of the guess
     * @param guess3 the third number of the guess
     * @param guess4 the fourth number of the guess
     * @param session the HTTP session containing the game
     * @param model the model to pass attributes to the view (not used here)
     * @return redirect to the main game page
     */
    @PostMapping("/guess")
    public String guess(@RequestParam("guess1") int guess1,
                        @RequestParam("guess2") int guess2,
                        @RequestParam("guess3") int guess3,
                        @RequestParam("guess4") int guess4,
                        HttpSession session,
                        Model model) {
        Game game = (Game) session.getAttribute("game");

        List<Integer> guessList = List.of(guess1, guess2, guess3, guess4);

        Feedback feedback = GuessChecker.checkGuess(game.getCode(), guessList);

        Guess guess = new Guess(guessList, feedback);

        game.addGuess(guess);

        return "redirect:/";
    }

    /**
     * Resets the current game by removing it from the session.
     * Redirects back to the main game page, which will initialize a new game.
     *
     * @param session the HTTP session containing the game
     * @return redirect to the main game page
     */
    @PostMapping("/reset")
    public String reset(HttpSession session) {
        session.removeAttribute("game");
        return "redirect:/";
    }

}
