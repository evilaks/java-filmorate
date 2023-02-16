package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.model.Rating;

public interface RatingStorage {
    Rating get(int id);
}
