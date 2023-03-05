package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping()
    public Review addReview(@RequestBody @Valid Review review) {
        log.debug("Received POST-request at \"/reviews\" endpoint with body: {}", review);
        return reviewService.addReview(review);
    }

    @PutMapping()
    public Review updateReview(@RequestBody @Valid Review review) {
        log.debug("Received PUT-request at \"/reviews\" endpoint with body: {}", review);
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{reviewId}")
    public void removeReview(@PathVariable Long reviewId) {
        log.debug("Received DELETE-request at \"/reviews/{}\" endpoint", reviewId);
        reviewService.deleteReview(reviewId);
    }

    @GetMapping("/{reviewId}")
    @ResponseBody
    public Review getReview(@PathVariable Long reviewId) {
        log.debug("Received GET-request at \"/reviews/{}\" endpoint", reviewId);
        return reviewService.getReview(reviewId);
    }

    @GetMapping()
    @ResponseBody
    public List<Review> findReviews(
            @RequestParam(defaultValue = "10", required = false) Integer count,
            @RequestParam(required = false) Long filmId
    ) {
        log.debug("Received GET-request at \"/reviews\" endpoint with params: count=" + count + ", filmId=" + filmId);
        if (filmId == null) {
            return reviewService.getAllReviews(count);
        } else {
            return reviewService.getReviewsByFilmId(count, filmId);
        }
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public void addPositiveMarkToReview(@PathVariable Long reviewId, @PathVariable Long userId) {
        log.debug("Received PUT-request at \"/reviews/{}/like/{}\" endpoint", reviewId, userId);
        reviewService.addMarkToReview(reviewId, userId, true);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public void addNegativeMarkToReview(@PathVariable Long reviewId, @PathVariable Long userId) {
        log.debug("Received PUT-request at \"/reviews/{}/dislike/{}\" endpoint", reviewId, userId);
        reviewService.addMarkToReview(reviewId, userId, false);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public void deleteMarkToReview(@PathVariable Long reviewId, @PathVariable Long userId) {
        log.debug("Received DELETE-request at \"/reviews/{}/dislike/{}\" endpoint", reviewId, userId);
        reviewService.deleteMarkFromReview(reviewId, userId, false);
    }

}