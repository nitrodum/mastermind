package com.example.service;

import org.springframework.stereotype.Service;
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
        String uri = "https://www.random.org/integers/?num=4&min=0&max=7&col=1&base=10&format=plain&rnd=new";

        String response = restTemplate.getForObject(uri, String.class);

        String[] lines = response.split("\\r?\\n");
        List<Integer> code = new ArrayList<Integer>();

        for(int i = 0; i < lines.length; i++) {
            code.add(Integer.parseInt(lines[i].trim()));
        }
        return code;
    }
}
