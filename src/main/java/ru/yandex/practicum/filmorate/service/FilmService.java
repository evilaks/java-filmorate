package ru.yandex.practicum.filmorate.service;

/*
Создайте FilmService, который будет отвечать за операции с фильмами, — добавление и удаление лайка,
вывод 10 наиболее популярных фильмов по количеству лайков. Пусть пока каждый пользователь может
поставить лайк фильму только один раз.
 */

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmService {
    FilmStorage filmStorage;
    UserStorage userStorage;

    public Film addLike(Long filmId, Long userId) {
        Film film = filmStorage.get(filmId);
        userStorage.get(userId); // check if user exist, else throw 404
        // todo check if film already has this lke
        film.addLike(userId);
        filmStorage.update(film);
        return film;
    }

    public Film removeLike(Long filmId, Long userId) {
        Film film = filmStorage.get(filmId);
        // todo check what's happened if like doesn't exist
        film.removeLike(userId);
        filmStorage.update(film);
        return film;
    }

    public List<Film> getPopularFilms() {
        Comparator<Film> filmComparator = (f1, f2) -> f2.getLikes().size() - f1.getLikes().size();
        return filmStorage.getAll().stream()
                .sorted(filmComparator)
                .limit(10)
                .collect(Collectors.toList());
    }
}
