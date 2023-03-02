package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public Review addReview(@RequestBody @Valid Review review) {
        log.debug("Received POST-request at \"/reviews\" endpoint with body: {}", review);
        return reviewService.addReview(review);
    }

    @PutMapping("/reviews")
    public Review updateReview(@RequestBody @Valid Review review) {
        log.debug("Received PUT-request at \"/reviews\" endpoint with body: {}", review);
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public void removeReview(@PathVariable Long reviewId) {
        log.debug("Received DELETE-request at \"/reviews/{}\" endpoint", reviewId);
        reviewService.deleteReview(reviewId);
    }

    @GetMapping("/reviews/{reviewId}")
    @ResponseBody
    public Review getReview(@PathVariable Long reviewId) {
        log.debug("Received GET-request at \"/reviews/{}\" endpoint", reviewId);
        return reviewService.getReview(reviewId);
    }


}
/*

        - `GET /reviews?filmId={filmId}&count={count}`
        Получение всех отзывов по идентификатору фильма, если фильм не указан то все. Если кол-во не указано то 10.

        - `PUT /reviews/{id}/like/{userId}`  — пользователь ставит лайк отзыву.
        - `PUT /reviews/{id}/dislike/{userId}`  — пользователь ставит дизлайк отзыву.
        - `DELETE /reviews/{id}/like/{userId}`  — пользователь удаляет лайк/дизлайк отзыву.
        - `DELETE /reviews/{id}/dislike/{userId}`  — пользователь удаляет дизлайк отзыву.

 */