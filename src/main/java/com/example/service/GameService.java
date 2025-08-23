package com.example.service;

import com.example.model.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    private final RandomOrgService randomOrgService;

    @Autowired
    public GameService(RandomOrgService randomOrgService) {
        this.randomOrgService = randomOrgService;
    }

    public Game createGame(int maxAttempts) {
        List<Integer> code = randomOrgService.getRandomCode();
        return new Game(code, maxAttempts);
    }
}
