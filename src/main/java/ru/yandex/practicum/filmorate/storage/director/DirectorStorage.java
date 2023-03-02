package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {

    List<Director> getAll();

    Director getDirector();

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Long directorId);
}
