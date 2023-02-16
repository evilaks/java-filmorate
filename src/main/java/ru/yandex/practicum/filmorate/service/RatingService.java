package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingStorage ratingStorage;

    public Rating getRating(int id) {
        Rating mpaRating = ratingStorage.get(id);
        if (mpaRating == null) {
            throw new NotFoundException("MPA rating with id=" + id + " not found");
        } else return mpaRating;
    }
}
