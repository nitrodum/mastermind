package com.example.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RandomOrgService {

    private final RestTemplate restTemplate;

    public RandomOrgService() {
        this.restTemplate = new RestTemplate();
    }

    public int[] getRandomCode(){
        String uri = "https://www.random.org/integers/?num=4&min=0&max=7&col=1&base=10&format=plain&rnd=new";

        String response = restTemplate.getForObject(uri, String.class);

        String[] lines = response.split("\\r?\\n");
        int[] code = new int[lines.length];

        for(int i = 0; i < lines.length; i++) {
            code[i] = Integer.parseInt(lines[i].trim());
        }
        return code;
    }
}
