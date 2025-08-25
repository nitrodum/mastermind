package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

public class RandomOrgServiceTests {

    private RandomOrgService randomOrgService;

    @BeforeEach
    public void setUp() {
        randomOrgService = new RandomOrgService();
    }

    @Test
    public void testGetRandomCodeSuccess() {
        List<Integer> code = randomOrgService.getRandomCode();

        assert code.size() == 4;
        for (Integer num : code) {
            assert num >= 0 && num <= 7;
        }
    }
}
