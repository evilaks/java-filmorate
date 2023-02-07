package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
public class FilmController {

    private FilmStorage filmStorage;
    private FilmService filmService;

    @GetMapping("/films")
    public List<Film> getFilms() {
        return filmStorage.getAll();
    }

    @GetMapping("/films/{filmId}")
    public Film getFilm(@PathVariable("filmId") Long filmId) {
        return filmStorage.get(filmId);
    }

    @PostMapping("/films")
    public Film addFilm(@RequestBody @Valid Film film) {
        return filmStorage.add(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.debug("Received PUT-request at /films endpoint with Film-object {}", film.toString());
        return filmStorage.update(film);
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public Film addLike(@PathVariable("filmId") Long filmId, @PathVariable("userId") Long userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/films/{filmId}/like/{userId}")
    public Film removeLike(@PathVariable("filmId") Long filmId, @PathVariable("userId") Long userId) {
        return filmService.removeLike(filmId, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getPopularFilms(count);
    }

}
