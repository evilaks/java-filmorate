package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmService filmService;
    private final UserService userService;

    public Review addReview(Review review) {
        validateReview(review);
        return reviewStorage.add(review);
    }

    public Review updateReview(Review review) {
        validateReview(review);
        this.getReview(review.getReviewId());     // check review existence
        return reviewStorage.update(review);
    }

    public void deleteReview(Long reviewId) {
        this.getReview(reviewId);                 // throws 404 if review doesn't exist
        reviewStorage.delete(reviewId);
    }

    public Review getReview(Long reviewId) throws NotFoundException {
        Review review = reviewStorage.get(reviewId);
        if (review == null) {
            throw new NotFoundException("Review with id=" + reviewId + " not found.");
        } else {
            return review;
        }
    }

    public List<Review> getAllReviews(Integer count) {
        return reviewStorage.getAll(count);
    }

    public List<Review> getReviewsByFilmId(Integer count, Long filmId) {
        filmService.getFilm(filmId); // throws 404 if film not found
        return reviewStorage.findReviewsByFilmId(count, filmId);
    }

    private void validateReview(Review review)  {
        filmService.getFilm(review.getFilmId());  // check film existence
        userService.getUser(review.getUserId());  // check user existence
    }

}
