package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmService filmService;
    private final UserService userService;
    private final UserStorage userStorage;

    public Review addReview(Review review) {
        validateReview(review);
        Review result = reviewStorage.add(review);
        userStorage.addEvent(result.getUserId(), "REVIEW", "ADD", result.getReviewId());
        return result;
    }

    public Review updateReview(Review review) {
        validateReview(review);
        Review reviewToUpdate = this.getReview(review.getReviewId());     // also check review existence
        reviewToUpdate.setContent(review.getContent());
        reviewToUpdate.setIsPositive(review.getIsPositive());
        Review result = reviewStorage.update(reviewToUpdate);
        userStorage.addEvent(result.getUserId(), "REVIEW", "UPDATE", result.getReviewId());
        return result;
    }

    public void deleteReview(Long reviewId) {
        this.getReview(reviewId);                 // throws 404 if review doesn't exist
        userStorage.addEvent(reviewStorage.get(reviewId).getUserId(), "REVIEW", "REMOVE", reviewId);
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

    public List<Review> findReviews(Integer count, Long filmId) {
        if (filmId == null) {
            return this.getAllReviews(count);
        } else {
            return this.getReviewsByFilmId(count, filmId);
        }
    }

    public List<Review> getAllReviews(Integer count) {
        return reviewStorage.getAll(count);
    }

    public List<Review> getReviewsByFilmId(Integer count, Long filmId) {
        filmService.getFilm(filmId); // throws 404 if film not found
        return reviewStorage.findReviewsByFilmId(count, filmId);
    }

    public void addMarkToReview(Long reviewId, Long userId, Boolean isUseful) {
        userService.getUser(userId); // throws 404 if user not found
        Review review = this.getReview(reviewId); // throws 404 if review not found
        try {
            reviewStorage.addMark(review, userId, isUseful);
        } catch (DuplicateKeyException e) {
            log.warn("Failed to add existing mark with userId={} and reviewId={}", userId, reviewId);
            throw new BadRequestException("Mark with such reviewId and userId is already exist");
        }
    }

    public void deleteMarkFromReview(Long reviewId, Long userId, Boolean isUseful) {
        userService.getUser(userId); // throws 404 if user not found
        Review review = this.getReview(reviewId); // throws 404 if review not found
        if (isUseful) {  // strange logic from the task :)
            reviewStorage.removeMark(review, userId, true);
            reviewStorage.removeMark(review, userId, false);
        } else {
            reviewStorage.removeMark(review, userId, false);
        }
    }

    private void validateReview(Review review) {
        filmService.getFilm(review.getFilmId());  // check film existence
        userService.getUser(review.getUserId());  // check user existence
    }

}
