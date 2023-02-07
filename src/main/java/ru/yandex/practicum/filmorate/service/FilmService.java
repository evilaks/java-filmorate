package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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

        if (!film.getLikes().contains(userId)) {
            film.addLike(userId);
            filmStorage.update(film);
        } else throw new BadRequestException("The film has already got like from user " + userId);

        return film;
    }

    public Film removeLike(Long filmId, Long userId) {
        Film film = filmStorage.get(filmId);
        userStorage.get(userId); // check if user exist, else throw 404

        if (film.getLikes().contains(userId)) {
            film.removeLike(userId);
            filmStorage.update(film);
        } else throw new NotFoundException("Film has no like from user " + userId);

        return film;
    }

    public List<Film> getPopularFilms(int count) {
        Comparator<Film> filmComparator = (f1, f2) -> f2.getLikes().size() - f1.getLikes().size();
        return filmStorage.getAll().stream()
                .sorted(filmComparator)
                .limit(count)
                .collect(Collectors.toList());
    }
}
