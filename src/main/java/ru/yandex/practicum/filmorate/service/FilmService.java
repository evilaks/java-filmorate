package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
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
    private final DirectorService directorService;
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

    public void deleteFilm(Long filmId) {
        Optional.ofNullable(filmStorage.get(filmId))
                .orElseThrow(() -> new NotFoundException("Film for userId " + filmId + " not found!"));
        filmStorage.deleteFilm(filmId);
    }

    public Film addLike(Long filmId, Long userId) {
        Film film = this.getFilm(filmId); // throws 404 if film doesn't exist
        userService.getUser(userId); // check if user exist, else throw 404

        if (!filmStorage.getLikes(film).contains(userId)) {
            filmStorage.addLike(film, userId);
        } else {
            log.debug("The film has already got like from user with id={}, " +
                    "but we still return 200OK according to postman tests %)", userId);
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

    public List<Film> getSortedFilmsFromDirector(Long directorId, String sortBy) {
        Director director = directorService.getDirector(directorId);
        if (sortBy.isBlank() || (!sortBy.equals("year") && !sortBy.equals("likes"))) {
            throw new BadRequestException("Bad request parameter 'sortBy'");
        }
        return filmStorage.getSortedFilmsFromDirector(director.getId(), sortBy);
    }

    public List<Film> searchFilms(String query, String by) {
        if (query.isBlank() || by.isBlank()
                || (!by.equals("director") && !by.equals("title")
                && !by.equals("director,title") && !by.equals("title,director"))) {
            log.debug("Incorrect parameters");
            throw new BadRequestException("Bad request parameter 'query' or 'by'");
        }
        return filmStorage.searchFilms(query, by);
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

    public List<Film> getFilmsSharedFilmAndSort(Long userId, Long friendId) { //?????????? ?????????? ?? ???????????? ?????????????? ?? ?????????????????????? ???? ???? ????????????????????????.
        if (userStorage.get(userId) == null || userStorage.get(friendId) == null) {
            log.debug("Invalid User ID: User ID is Not found");
            throw new ValidationException("Bad request parameter 'sortBy'");
        }
        List<Long> filmLikesUserId = new ArrayList<>(filmStorage.getIdFilmsWithUserLikes(userId));
        List<Long> filmLikesFriendsId = new ArrayList<>(filmStorage.getIdFilmsWithUserLikes(friendId));
        List<Film> mutualFilmList = new ArrayList<>();
        for (long t : filmLikesUserId) {
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

    private void isInvalidCountOrGenreIdOrYear(int count, int genreId, int year) {
        if (count < 1) {
            log.debug("Invalid Count: Count is < 1");
            throw new ValidationException("Bad request parameter 'sortBy'");
        }
        if (genreId < 0) {
            log.debug("Invalid Genre ID: Genre ID < 1");
            throw new ValidationException("Bad request parameter 'sortBy'");
        }
        if (year < 0) {
            log.debug("Invalid Year: Year < 1");
            throw new ValidationException("Bad request parameter 'sortBy'");
        }
    }

    public List<Film> getPopularFilmGenreIdYear(int count, int genreId, int year) {
        isInvalidCountOrGenreIdOrYear(count, genreId, year);
        List<Long> filmIdSorted = new ArrayList<>(filmStorage.getPopularFilmGenreIdYear(year, genreId, count));
        List<Film> filmListSorted = new ArrayList<>();
        for (long t : filmIdSorted) {
            filmListSorted.add(filmStorage.get(t));
        }

        return filmListSorted;
    }

    public List<Film> getRecommendations(Long userId) {
        //???????????????? ???????? ???? ????????????????????????, ?????? ???????????????? ???????????????????? ????????????????????????
        userService.getUser(userId);
        return filmStorage.getRecommendations(userId);
    }
}
