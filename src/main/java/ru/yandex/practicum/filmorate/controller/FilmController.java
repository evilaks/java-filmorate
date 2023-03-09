package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable("filmId") Long filmId) {
        return filmService.getFilm(filmId);
    }

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.debug("Received PUT-request at /films endpoint with Film-object {}", film.toString());
        return filmService.updateFilm(film);
    }

    @DeleteMapping("{filmId}")
    public void deleteFilm(@PathVariable Long filmId) {
        log.debug("Received DELETE-request at /films/{} endpoint", filmId);
        filmService.deleteFilm(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable("filmId") Long filmId, @PathVariable("userId") Long userId) {
        return filmService.addLike(filmId, userId);
    }

    @GetMapping("/{filmId}/likes")
    public List<Long> getFilmLikes(@PathVariable("filmId") Long filmId) {
        return filmService.getLikesByFilm(filmId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film removeLike(@PathVariable("filmId") Long filmId, @PathVariable("userId") Long userId) {
        return filmService.removeLike(filmId, userId);
    }

    @GetMapping("/common") //films/common?userId={userId}&friendId={friendId}
    @ResponseBody
    public List<Film> getMoviesSharedFilmAndSort(@RequestParam(value = "userId") Long userId,
                                                 @RequestParam(value = "friendId") Long friendId) {
        return filmService.getFilmsSharedFilmAndSort(userId, friendId);
    }

    @GetMapping("/popular") //films/popular?count={limit}&genreId={genreId}&year={year}

    public List<Film> getPopularFilmGenreIdYear(@RequestParam(defaultValue = "10") Integer count,
                                                @RequestParam(defaultValue = "0") Integer genreId,
                                                @RequestParam(defaultValue = "0") Integer year) {
        return filmService.getPopularFilmGenreIdYear(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getSortedFilmsFromDirector(@PathVariable Long directorId,
                                                 @RequestParam String sortBy) {
        log.debug("Extracting films by director with id={} sorted by {}", directorId, sortBy);
        return filmService.getSortedFilmsFromDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query, @RequestParam String by) {
        log.debug("Received GET-request at /films/search endpoint with query {} and searching in {}", query, by);
        return filmService.searchFilms(query, by);
    }
}
