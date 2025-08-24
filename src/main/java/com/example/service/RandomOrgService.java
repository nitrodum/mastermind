package com.example.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class RandomOrgService {

    private final RestTemplate restTemplate;

    public RandomOrgService() {
        this.restTemplate = new RestTemplate();
    }

    public List<Integer> getRandomCode(){
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
            System.out.println(e.getMessage());
            code = List.of(1, 2, 1, 2);
        }
        return code;
    }
}
