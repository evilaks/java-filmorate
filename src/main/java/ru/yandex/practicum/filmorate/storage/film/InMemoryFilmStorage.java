package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

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
        filmId++;
        film.setId(filmId);
        films.put(filmId, film);
        log.debug("Created film record with Film-object {}", film);
        return film;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film get(long id) {
        return films.getOrDefault(id, null);
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        log.debug("Updated film-record with id={}", film.getId());
        return film;
    }

    @Override
    public void remove(Film film) {
        log.debug("Unimplemented \"remove\" method called");
    }

    @Override
    public void addLike(Film film, long userId) {
        log.debug("Unimplemented \"addLike\" method called");
    }

    @Override
    public List<Long> getLikes(Film film) {
        log.debug("Unimplemented \"getLike\" method called");
        return null;
    }

    @Override
    public void removeLike(Film film, long userId) {
        log.debug("Unimplemented \"removeLike\" method called");
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        log.debug("Unimplemented \"getPopularFilms\" method called");
        return null;
    }
}
