package com.example.service;

import com.example.model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class GameServiceTests {

    @Mock
    private RandomOrgService randomOrgService;
    private GameService gameService;

    @BeforeEach
    public void setUp() {
        randomOrgService = org.mockito.Mockito.mock(RandomOrgService.class);
        gameService = new GameService(randomOrgService);
    }

    @Test
    public void testCreateGameSuccess() {
        List<Integer> mockCode = List.of(1, 2, 3, 4);
        when(randomOrgService.getRandomCode()).thenReturn(mockCode);

        Game game = gameService.createGame(10);

        assertEquals(mockCode, game.getCode());
        assertEquals(10, game.getMaxAttempts());
        assertEquals(0, game.getAttempts());
        assertTrue(game.getGuesses().isEmpty());
    }
}
