package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Long, Film> films = new HashMap<>();
    private long filmId = 0;

    @Override
    public Film add(Film film) {
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

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film get(long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else throw new NotFoundException("Film not found");
    }

    @Override
    public Film update(Film film) {
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

    @Override
    public void remove(Film film) {
        log.debug("Unimplemented \"remove\" method called");
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
