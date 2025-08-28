package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class RandomOrgService {

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(RandomOrgService.class);
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 500;

    public RandomOrgService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Fetches a list of 4 random integers between 0 and 7 from Random.org with retry logic.
     * If all retry attempts fail, falls back to local random generation instead.
     *
     * @return a list of 4 random integers between 0 and 7
     */
    public List<Integer> getRandomCode() {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {

            try {
                List<Integer> code;

                String url = "https://www.random.org/integers/?num=4&min=0&max=7&col=1&base=10&format=plain&rnd=new";

                HttpHeaders headers = new HttpHeaders();
                headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
                headers.set("Accept", "text/plain, text/html, */*");
                headers.set("Accept-Language", "en-US,en;q=0.9");
                headers.set("Connection", "keep-alive");

                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

                if (response.getBody() != null) {
                    String[] lines = response.getBody().split("\\r?\\n");
                    code = new ArrayList<>();


                    for (String line : lines) {
                        code.add(Integer.parseInt(line.trim()));
                    }

                    if (code.size() == 4) {
                        logger.info("Successfully fetched random numbers from Random.org: {}", code);
                        return code;
                    }
                }
            } catch (HttpServerErrorException e) {
                logger.warn("Error fetching random numbers from Random.org: {}", e.getMessage());

                if (attempt == MAX_RETRIES) {
                    logger.error("All attempts to fetch random numbers failed.");
                } else {
                    try {
                        Thread.sleep(RETRY_DELAY_MS * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        logger.info("Using local random generation as fallback after {} failed attempts", MAX_RETRIES);
        return generateLocalRandomCode();
    }

    private List<Integer> generateLocalRandomCode() {
        Random random = new Random();
        return random.ints(4, 0, 8).boxed().toList();
    }
}
