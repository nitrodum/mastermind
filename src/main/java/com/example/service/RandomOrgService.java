package com.example.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@Service
public class RandomOrgService {

    public int[] getRandomCode(){
        return new int[4];
    }
}
