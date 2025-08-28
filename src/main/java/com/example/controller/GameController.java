package com.example.controller;

import com.example.model.*;
import com.example.service.*;
import com.example.util.GuessChecker;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;


@Controller
public class GameController {
    private final GameService gameService;
    private final UserService userService;
    private final DailyGameService dailyGameService;
    private final DailySubmissionService dailySubmissionService;
    private final DailyStatsService dailyStatsService;
    private final UserStatsService userStatsService;

    @Autowired
    public GameController(GameService gameService, UserService userService, DailyGameService dailyGameService, DailySubmissionService dailySubmissionService, DailyStatsService dailyStatsService, UserStatsService userStatsService) {
        this.gameService = gameService;
        this.userService = userService;
        this.dailyGameService = dailyGameService;
        this.dailySubmissionService = dailySubmissionService;
        this.dailyStatsService = dailyStatsService;
        this.userStatsService = userStatsService;
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
        User user = (User) session.getAttribute("user");
        String gameMode = (String) session.getAttribute("gameMode");

        if (user != null ) {
            model.addAttribute("username", user.getUsername());

            if (gameMode.equals("daily")) {
                Date today = dailyGameService.getTodayDate();
                if (dailySubmissionService.hasUserSubmittedToday(user, today)) {
                    model.addAttribute("alreadySubmitted", true);
                    return "daily-complete";
                }
            }
        }

        if (gameMode == null) {
            gameMode = (user != null) ? "daily" : "infinite";
            session.setAttribute("gameMode", gameMode);
        }

        model.addAttribute("gameMode", gameMode);

        if (game == null) {
            if (gameMode.equals("daily")) {
                game = createDailyGame();
            } else {
                game = gameService.createGame(10);
            }
            session.setAttribute("game", game);
        }

        checkGameCompletion(game, user, session, model);

        model.addAttribute("maxAttempts", game.getMaxAttempts());
        model.addAttribute("attempts", game.getAttempts());
        model.addAttribute("code", game.getCode());
        model.addAttribute("guesses", game.getGuesses());

        return "index";
    }

    @GetMapping("/mode/{mode}")
    public String switchMode(@PathVariable String mode, HttpSession session) {
        if (mode.equals("daily") || mode.equals("infinite")) {
            session.setAttribute("gameMode", mode);
            session.removeAttribute("game");
        }
        return "redirect:/";
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
        User user = (User) session.getAttribute("user");
        String gameMode = (String) session.getAttribute("gameMode");

        if (user != null && gameMode.equals("daily")) {
            Date today = dailyGameService.getTodayDate();
            if(dailySubmissionService.hasUserSubmittedToday(user, today)) {
                model.addAttribute("alreadySubmitted", true);
                return "daily-complete";
            }
        }

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

    private void checkGameCompletion(Game game, User user, HttpSession session, Model model) {
        if (game.getGuesses().isEmpty() || game.isOver()) {
            return;
        }

        Guess lastGuess = game.getGuesses().get(game.getGuesses().size() - 1);
        boolean isWin = lastGuess.getFeedback().getCorrectNumbers() == 4 &&
                        lastGuess.getFeedback().getCorrectPositions() == 4;
        boolean isLoss = game.getAttempts() >= game.getMaxAttempts();

        if (isWin) {
            handleGameWin(game, user, session, model);
        } else if (isLoss) {
            handleGameLoss(game, user, session, model);
        }
    }

    private void handleGameWin(Game game, User user, HttpSession session, Model model) {
        model.addAttribute("status", "You Win! :)");
        game.setOver(true);

        if (user != null) {
            if (session.getAttribute("gameMode").equals("daily")) {
                Date today = dailyGameService.getTodayDate();

                if(dailySubmissionService.hasUserSubmittedToday(user, today)) {
                    return;
                }

                updateDailyStatsForWin(game, user, session);
                DailySubmission submission = new DailySubmission(
                        game.getAttempts(),
                        new Date(),
                        today,
                        user
                );
                dailySubmissionService.saveDailySubmission(submission);
            } else {
               updateInfiniteStatsForWin(game, user, session);
            }
        }
    }

    private void handleGameLoss(Game game, User user, HttpSession session, Model model) {
        model.addAttribute("status", "You Lose! :(");
        game.setOver(true);

        if (user != null) {
            if (session.getAttribute("gameMode").equals("daily")) {
                Date today = dailyGameService.getTodayDate();

                if(dailySubmissionService.hasUserSubmittedToday(user, today)) {
                    return;
                }

                updateDailyStatsForLoss(game, user, session);
                DailySubmission submission = new DailySubmission(
                        game.getAttempts(),
                        new Date(),
                        today,
                        user
                );
                dailySubmissionService.saveDailySubmission(submission);
            } else {
                updateInfiniteStatsForLoss(game, user, session);
            }
        }
    }

    private void updateDailyStatsForWin(Game game, User user, HttpSession session) {
        DailyStats dailyStats = dailyStatsService.getDailyStatsByUser(user);
        if (dailyStats == null) {
            dailyStats = new DailyStats(user);
        }
        dailyStats.gameWon(game.getAttempts());
        dailyStatsService.saveDailyStats(dailyStats);
        user.setDailyStats(dailyStats);
        session.setAttribute("user", user);
    }

    private void updateInfiniteStatsForWin(Game game, User user, HttpSession session) {
        UserStats stats = user.getUserStats();
        if (stats == null) {
            stats = new UserStats(user);
        }
        stats.gameWon(game.getAttempts());
        userStatsService.saveUserStats(stats);
        user.setUserStats(stats);
        session.setAttribute("user", user);
    }

    private void updateDailyStatsForLoss(Game game, User user, HttpSession session) {
        DailyStats dailyStats = dailyStatsService.getDailyStatsByUser(user);
        if (dailyStats == null) {
            dailyStats = new DailyStats(user);
        }
        dailyStats.gameLost(game.getAttempts());
        dailyStatsService.saveDailyStats(dailyStats);
        user.setDailyStats(dailyStats);
        session.setAttribute("user", user);
    }

    private void updateInfiniteStatsForLoss(Game game, User user, HttpSession session) {
        UserStats stats = user.getUserStats();
        if (stats == null) {
            stats = new UserStats(user);
        }
        stats.gameLost(game.getAttempts());
        userStatsService.saveUserStats(stats);
        user.setUserStats(stats);
        session.setAttribute("user", user);
    }

    private Game createDailyGame() {
        DailyGame todayGame = dailyGameService.getTodaysDailyGame();
        return new Game(todayGame.getCodeAsList(), 10);
    }
}
