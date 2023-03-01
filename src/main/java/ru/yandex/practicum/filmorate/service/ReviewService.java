package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;

    public Review addReview(Review review) {
        return null;
    }

    public Review updateReview(Review review) {
        return null;
    }

    public void deleteReview(Long reviewId) {

    }

    public Review getReview(Long reviewId) {
        return null;
    }

}
