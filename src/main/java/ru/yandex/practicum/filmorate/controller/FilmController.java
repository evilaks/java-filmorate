package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private int filmId = 0;

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        log.debug("Received POST-request with Film object: {}", film.toString());
        if (films.containsKey(film.getId())) {
            log.debug("Film with such id already exist");
            throw new BadRequestException("Film with such id already exist");
        } else if (!isFilmValid(film)) {
            throw new ValidationException("Invalid film-object received");
        } else {
            filmId++;
            film.setId(filmId);
            films.put(filmId, film);
            log.debug("Created film record with Film-object {}", film);
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.debug("Received PUT-request at /films endpoint with Film-object {}", film.toString());
        if (!isFilmValid(film)) {
            throw new ValidationException("Invalid film-object received");
        } else if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Updated film record with id {}", film.getId());
        } else {
            log.debug("Film with such id not found");
            throw new NotFoundException("Film with such id not found");
        }
        return film;
    }

    private boolean isFilmValid(Film film) {
        if (film.getName().isBlank()) {
            log.debug("Invalid Film-object: filmName is blank");
            return false;
        }
        if (film.getDescription().length() > 200) {
            log.debug("Invalid Film-object: film description is too long");
            return false;
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Invalid Film-object: too early release date");
            return false;
        }
        if (film.getDuration() <= 0) {
            log.debug("Invalid Film-object: duration is <= 0");
            return false;
        }
        return true;
    }
}
