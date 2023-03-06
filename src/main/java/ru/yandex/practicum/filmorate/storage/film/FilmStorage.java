package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film add(Film film);

    List<Film> getAll();

    Film get(long id);

    Film update(Film film);

    void deleteFilm(Long filmId);

    void deleteAll();

    void addLike(Film film, long userId);

    List<Long> getLikes(Film film);

    void removeLike(Film film, long userId);

    List<Film> getPopularFilms(int count);

    List<Long> getFilmLikes(Film film);

    List<Long> getIdFilmsWithUserLikes(Long userId);

    List<Film> getSortedFilmsFromDirector(Long directorId, String sortBy);

    List<Film> searchFilms(String query, String by);

    List<Film> getRecommendations(Long userId);
}
