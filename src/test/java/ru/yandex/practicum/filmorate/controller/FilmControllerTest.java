package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;

public class FilmControllerTest {

    private Film testFilm;
    private FilmController testFilmController;

    @BeforeEach
    void beforeEach() {
        testFilm = new Film();
        testFilm.setName("name");
        testFilm.setDescription("desc");
        testFilm.setReleaseDate(LocalDate.of(2000, 10, 10));
        testFilm.setDuration(100);

        FilmStorage testFilmStorage = new InMemoryFilmStorage();
        testFilmController = new FilmController(testFilmStorage);
    }

    @Test
    void addFilm() {
        Film actual = testFilmController.addFilm(testFilm);
        assertEquals(1, actual.getId(), "Film object doesn't get id from filmController");

        testFilm.setId(1);
        assertThrows(BadRequestException.class, () -> testFilmController.addFilm(testFilm), "id already exist");

        testFilm.setId(0);
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

        testFilm.setId(1);
        assertEquals(testFilm, actualFilm, "Added film objects doesn't match");

        testFilm.setName("new name");
        testFilm.setDescription("new description");
        testFilm.setDuration(200);
        testFilm.setReleaseDate(LocalDate.of(2020, 1, 1));

        Film actual = testFilmController.updateFilm(testFilm);
        assertEquals(testFilm, actual, "Updated film objects doesn't match");

        testFilm.setId(2);
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

        testFilm.setId(1);
        Film actual = testFilmController.getFilms().get(0);
        assertEquals(testFilm, actual, "Returning film doesn't match added one");
    }
}
