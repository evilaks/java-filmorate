package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final GenreService genreService;

    private final UserStorage userStorage;

    public List<Film> getAllFilms() {
        return filmStorage.getAll()
                .stream()
                .sorted((f1, f2) -> Math.toIntExact((f1.getId() - f2.getId())))
                .collect(Collectors.toList());
    }

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
        return filmStorage.add(this.normalizeGenresInFilm(film));
    }

    public Film updateFilm(Film film) {
        if (isInvalidFilm(film)) {
            throw new ValidationException("Invalid user properties");
        } else if (filmStorage.get(film.getId()) == null) {
            throw new NotFoundException("Film with such id not found");
        } else {
            return filmStorage.update(this.normalizeGenresInFilm(film));
        }

    }

    public Film addLike(Long filmId, Long userId) {
        Film film = this.getFilm(filmId); // throws 404 if film doesn't exist
        userService.getUser(userId); // check if user exist, else throw 404

        if (!filmStorage.getLikes(film).contains(userId)) {
            filmStorage.addLike(film, userId);
        } else {
            throw new BadRequestException("The film has already got like from user " + userId);
        }
        userStorage.addEvent(userId, "LIKE", "ADD", filmId);
        return film;
    }

    public List<Long> getLikesByFilm(Long filmId) {
        Film film = this.getFilm(filmId); // throws 404 if film doesn't exist
        return filmStorage.getFilmLikes(film);
    }

    public Film removeLike(Long filmId, Long userId) {
        Film film = this.getFilm(filmId); // throws 404 if film doesn't exist
        userService.getUser(userId); // check if user exist, else throw 404

        if (filmStorage.getLikes(film).contains(userId)) {
            filmStorage.removeLike(film, userId);
        } else {
            throw new NotFoundException("Film has no like from user " + userId);
        }
        userStorage.addEvent(userId, "LIKE", "REMOVE", filmId);

        return film;
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private Film normalizeGenresInFilm(Film film) {
        if (film.getGenres() != null) {
            film.setGenres(new ArrayList<>(new HashSet<>(film.getGenres())));
        }
        return film;
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
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (genreService.getGenre(genre.getId()) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Film> getFilmsSharedFilmAndSort(Long userId, Long friendId){ //вывод общих с другом фильмов с сортировкой по их популярности.
        List<Long> filmLikesUserId = new ArrayList<>(filmStorage.getIdFilmsWithUserLikes(userId));
        List<Long> filmLikesFriendsId = new ArrayList<>(filmStorage.getIdFilmsWithUserLikes(friendId));
        List<Film> mutualFilmList = new ArrayList<>();
        for(long t: filmLikesUserId){
            if (filmLikesFriendsId.contains(t)) {
                mutualFilmList.add(filmStorage.get(t));
            }
        }
        Collections.sort(mutualFilmList, new Comparator<Film>() {
            @Override
            public int compare(Film o1, Film o2) {
                return o1.getLikes().size() - o2.getLikes().size();
            }
        });
        return mutualFilmList;

    }

}
