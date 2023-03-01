package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;

    public Review addReview(Review review) {
        // todo check film existence
        // todo check user existence
        return reviewStorage.add(review);
    }

    public Review updateReview(Review review) {
        // todo check film existence
        // todo check user existence
        // todo check review existence
        return reviewStorage.update(review);
    }

    public void deleteReview(Long reviewId) {
        // todo check if exists
        reviewStorage.delete(reviewId);
    }

    public Review getReview(Long reviewId) {
        Review review = reviewStorage.get(reviewId);
        if (review == null) {
            throw new NotFoundException("Review with id= " + reviewId + " not found.");
        } else {
            return review;
        }
    }

}
