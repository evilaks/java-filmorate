package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.director.DbDirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.DbGenreStorage;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {

    private Film testFilm;
    private FilmController testFilmController;

    @BeforeEach
    void beforeEach() {
        testFilm = Film.builder()
                .name("name")
                .description("desc")
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(100)
                .build();

        FilmStorage testFilmStorage = new InMemoryFilmStorage();
        UserStorage testUserStorage = new InMemoryUserStorage();
        UserService testUserService = new UserService(testUserStorage);
        FilmService testFilmService = new FilmService(
                testFilmStorage,
                testUserService,
                new GenreService(new DbGenreStorage(new JdbcTemplate())),
                new DirectorService(new DbDirectorStorage(new JdbcTemplate()))
        , new DbUserStorage(new JdbcTemplate()));
        testFilmController = new FilmController(testFilmService);
    }

    @Test
    void addFilm() {
        Film actual = testFilmController.addFilm(testFilm);
        assertEquals(1, actual.getId(), "Film object doesn't get id from filmController");

        testFilm.setId(0L);
        testFilm.setDuration(-1);
        assertThrows(ValidationException.class, () -> testFilmController.addFilm(testFilm), "Wrong duration");

        testFilm.setDuration(100);
        testFilm.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi " +
                "ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit " +
                "in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui " +
                "officia deserunt mollit anim id est laborum. " +
                "DESCRIPTION OVER TWO HUNDRED CHARACTERS");
        assertThrows(ValidationException.class, () -> testFilmController.addFilm(testFilm), "Wrong description");

        testFilm.setDescription("description");
        testFilm.setName("");
        assertThrows(ValidationException.class, () -> testFilmController.addFilm(testFilm), "Blank film name");

        testFilm.setName("name");
        testFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> testFilmController.addFilm(testFilm), "Wrong release date");
    }

    @Test
    void updateFilm() {
        Film actualFilm = testFilmController.addFilm(testFilm);

        testFilm.setId(1L);
        assertEquals(testFilm, actualFilm, "Added film objects doesn't match");

        testFilm.setName("new name");
        testFilm.setDescription("new description");
        testFilm.setDuration(200);
        testFilm.setReleaseDate(LocalDate.of(2020, 1, 1));

        Film actual = testFilmController.updateFilm(testFilm);
        assertEquals(testFilm, actual, "Updated film objects doesn't match");

        testFilm.setId(2L);
        assertThrows(NotFoundException.class, () -> testFilmController.updateFilm(testFilm), "id not found");

        actual.setDuration(-1);
        assertThrows(ValidationException.class, () -> testFilmController.updateFilm(actual), "Wrong duration");

        actual.setDuration(200);
        actual.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi " +
                "ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit " +
                "in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui " +
                "officia deserunt mollit anim id est laborum. " +
                "DESCRIPTION OVER TWO HUNDRED CHARACTERS");
        assertThrows(ValidationException.class, () -> testFilmController.updateFilm(actual), "Wrong description");

        actual.setDescription("description");
        actual.setName("");
        assertThrows(ValidationException.class, () -> testFilmController.updateFilm(actual), "Blank film name");

        actual.setName("name");
        actual.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> testFilmController.updateFilm(actual), "Wrong release date");
    }

    @Test
    void getFilms() {
        testFilmController.addFilm(testFilm);

        assertEquals(1, testFilmController.getFilms().size(), "Wrong size of returning films array");

        testFilm.setId(1L);
        Film actual = testFilmController.getFilms().get(0);
        assertEquals(testFilm, actual, "Returning film doesn't match added one");
    }
}
