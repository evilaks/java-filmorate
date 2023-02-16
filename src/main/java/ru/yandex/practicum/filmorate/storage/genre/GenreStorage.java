package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

public interface GenreStorage {
    Genre get(int id);
}
