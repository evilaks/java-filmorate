package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewStorage {
    Review add(Review review);

    Review update(Review review);

    Review get(Long id);

    void delete(Long id);
}
