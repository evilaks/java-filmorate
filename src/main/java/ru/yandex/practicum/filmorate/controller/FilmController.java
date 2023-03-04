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
@RequestMapping
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping("/films")
    public List<Film> getFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/films/{filmId}")
    public Film getFilm(@PathVariable("filmId") Long filmId) {
        return filmService.getFilm(filmId);
    }

    @PostMapping("/films")
    public Film addFilm(@RequestBody @Valid Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.debug("Received PUT-request at /films endpoint with Film-object {}", film.toString());
        return filmService.updateFilm(film);
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public Film addLike(@PathVariable("filmId") Long filmId, @PathVariable("userId") Long userId) {
        return filmService.addLike(filmId, userId);
    }

    @GetMapping("/films/{filmId}/likes")
    public List<Long> getFilmLikes(@PathVariable("filmId") Long filmId) {
        return filmService.getLikesByFilm(filmId);
    }

    @DeleteMapping("/films/{filmId}/like/{userId}")
    public Film removeLike(@PathVariable("filmId") Long filmId, @PathVariable("userId") Long userId) {
        return filmService.removeLike(filmId, userId);
    }

    @GetMapping( "/films/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping( "/films/common") //films/common?userId={userId}&friendId={friendId}
    public List<Film> getMoviesSharedFilmAndSort(@RequestParam (value = "userId") Long userId,
                                                 @RequestParam (value = "friendId") Long friendId){
        return filmService.getFilmsSharedFilmAndSort(userId, friendId);
    }

    @GetMapping(value = "/films/popular", params = {"count", "genreId", "year"}) //films/popular?count={limit}&genreId={genreId}&year={year}
    @ResponseBody
    public List<Film> getPopularFilmGenreIdYear(@RequestParam (value = "count") Integer count,
                                                @RequestParam (value = "genreId") Integer genreId,
                                                @RequestParam (value = "year") Integer year){
            return filmService.getPopularFilmGenreIdYear(count, genreId, year);
        }
    }
