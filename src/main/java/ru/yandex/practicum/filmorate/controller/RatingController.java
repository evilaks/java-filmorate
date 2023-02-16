package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RatingController {
    private final RatingStorage ratingStorage;
    private final RatingService ratingService;

    @GetMapping("/mpa")
    public List<Rating> getAllMpa() {
        return ratingStorage.getAll();
    }

    @GetMapping("/mpa/{id}")
    public Rating getMpa(@PathVariable("id") int ratingId) {
        return ratingService.getRating(ratingId);
    }
}
