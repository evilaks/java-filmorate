package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film getFilm(long filmId) {
        if (filmStorage.get(filmId) == null) {
            throw new NotFoundException("Film with id= " + filmId + " not found");
        }
        return filmStorage.get(filmId);
    }

    public Film addFilm(Film film) {
        if (isInvalidFilm(film)) {
            throw new ValidationException("Invalid film-object received");
        }
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        if (isInvalidFilm(film)) {
            throw new ValidationException("Invalid user properties");
        } else if (filmStorage.get(film.getId()) != null) {
            filmStorage.update(film);
        } else {
            throw new NotFoundException("Film with such id not found");
        }
        return film;
    }

    public Film addLike(Long filmId, Long userId) {
        Film film = filmStorage.get(filmId);
        userService.getUser(userId); // check if user exist, else throw 404

        if (!film.getLikes().contains(userId)) {
            film.addLike(userId);
            filmStorage.update(film);
        } else throw new BadRequestException("The film has already got like from user " + userId);

        return film;
    }

    public Film removeLike(Long filmId, Long userId) {
        Film film = filmStorage.get(filmId);
        userService.getUser(userId); // check if user exist, else throw 404

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

    private boolean isInvalidFilm(Film film) {
        if (film.getName().isBlank()) {
            log.debug("Invalid Film-object: filmName is blank");
            return true;
        }
        if (film.getDescription().length() > 200) {
            log.debug("Invalid Film-object: film description is too long");
            return true;
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Invalid Film-object: too early release date");
            return true;
        }
        if (film.getDuration() <= 0) {
            log.debug("Invalid Film-object: duration is <= 0");
            return true;
        }
        return false;
    }
}
