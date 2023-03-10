package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review add(Review review);

    Review update(Review review);

    Review get(Long id);

    List<Review> getAll(Integer count);

    List<Review> findReviewsByFilmId(Integer count, Long filmId);

    void addMark(Review review, Long userId, Boolean isUseful);

    void removeMark(Review review, Long userId, Boolean isUseful);

    void delete(Long id);


}
