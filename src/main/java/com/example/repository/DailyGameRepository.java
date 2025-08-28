package com.example.repository;

import com.example.model.DailyGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface DailyGameRepository extends JpaRepository<DailyGame, Date> {
}
