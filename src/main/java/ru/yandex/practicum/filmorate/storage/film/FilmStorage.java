package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

public interface FilmStorage {

    Film add(Film film);

    List<Film> getAll();

    Film get(long id);

    Film update(Film film);

    void remove(Film film);

    void deleteAll();

    void addLike(Film film, long userId);

    List<Long> getLikes(Film film);

    void removeLike(Film film, long userId);

    List<Film> getPopularFilms(int count);

    List<Long> getFilmLikes(Film film);

    List<Long> getIdFilmsWithUserLikes (Long userId);

    List<Long> getPopularFilmGenreIdYear (int count, int genreId, int year);

}
