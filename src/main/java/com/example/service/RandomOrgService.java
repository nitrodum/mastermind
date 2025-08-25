package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class RandomOrgService {

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(RandomOrgService.class);

    public RandomOrgService() {
        this.restTemplate = new RestTemplate();
    }

    /**
         * Fetches a list of 4 random integers between 0 and 7 from Random.org.
         * If the request fails due to a server error, returns a default list [1, 2, 1, 2].
         *
         * @return a list of 4 random integers, or a default list if an error occurs
         */
        public List<Integer> getRandomCode() {
            List<Integer> code = new ArrayList<>();

            try {
                String uri = "https://www.random.org/integers/?num=4&min=0&max=7&col=1&base=10&format=plain&rnd=new";

                String response = restTemplate.getForObject(uri, String.class);

                String[] lines = response.split("\\r?\\n");
                code = new ArrayList<>();

                for (String line : lines) {
                    code.add(Integer.parseInt(line.trim()));
                }
            } catch (HttpServerErrorException e) {
                logger.error("Error fetching random numbers from Random.org: {}", e.getMessage());
                code = List.of(1, 2, 1, 2);
            }
            return code;
        }
}
