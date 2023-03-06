package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.GenreController;
import ru.yandex.practicum.filmorate.controller.RatingController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final UserController userController;
    private final UserStorage userStorage;
    private final FilmController filmController;
    private final FilmStorage filmStorage;
    private final GenreController genreController;
    private final RatingController ratingController;

    @Test
    public void contextLoads() {
    }

    @BeforeEach
    public void beforeEach() {
        userController.addUser(createNewUser());
        filmController.addFilm(createNewFilm());
    }

    @AfterEach
    public void afterEach() {
        userStorage.deleteAll();
        filmStorage.deleteAll();
    }


    // User tests
    @Test
    public void addNewUser() {
        assertThat(Optional.of(userController.addUser(createNewUser())))
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 2L)
                );

    }

    @Test
    public void addInvalidUser() {
        assertThrows(ValidationException.class, () -> userController.addUser(User.builder()
                .name(" ")
                .login(" ")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2000, 1, 1))
                .build()), "testing user add invalid name");

        assertThrows(ValidationException.class, () -> userController.addUser(User.builder()
                .name("name")
                .login("login")
                .email(" ")
                .birthday(LocalDate.of(2000, 1, 1))
                .build()), "testing user add invalid email");

        assertThrows(ValidationException.class, () -> userController.addUser(User.builder()
                .name("name")
                .login("login")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2200, 1, 1))
                .build()), "testing user add invalid birthday");
    }

    @Test
    public void getUserById() {
        Optional<User> userOptional = Optional.of(userController.getUser(1L));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void updateUser() {
        User updatedUser = User.builder()
                .name("newname")
                .login("newlogin")
                .email("newemail@mail.ru")
                .birthday(LocalDate.of(1999, 1, 11))
                .id(1L)
                .build();

        Optional<User> actual = Optional.of(userController.updateUser(updatedUser));

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("name", "newname")
                                .hasFieldOrPropertyWithValue("login", "newlogin")
                                .hasFieldOrPropertyWithValue("email", "newemail@mail.ru")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1999, 1, 11))
                                .hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    public void updateInvalidUser() {
        assertThrows(ValidationException.class, () -> userController.updateUser(User.builder()
                .id(1L)
                .name(" ")
                .login(" ")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2000, 1, 1))
                .build()), "testing user update invalid name");

        assertThrows(ValidationException.class, () -> userController.updateUser(User.builder()
                .id(1L)
                .name("name")
                .login("login")
                .email(" ")
                .birthday(LocalDate.of(2000, 1, 1))
                .build()), "testing user update invalid email");

        assertThrows(ValidationException.class, () -> userController.updateUser(User.builder()
                .id(1L)
                .name("name")
                .login("login")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2200, 1, 1))
                .build()), "testing user update invalid birthday");
    }

    @Test
    public void updateUnknownUser() {
        assertThrows(NotFoundException.class, () -> userController.updateUser(User.builder()
                .id(9999L)
                .name("name")
                .login("login")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2000, 1, 1))
                .build()), "testing user update unknown");
    }

    @Test
    public void getAllUsers() {

        User userOne = userController.getUser(1L);
        User userTwo = userController.addUser(createNewUser());

        List<User> users = userController.getUsers();

        assertThat(users).contains(userOne, userTwo);
    }

    @Test
    public void addFriend() {
        User userTwo = userController.addUser(createNewUser());

        userController.addFriend(1L, 2L);

        assertThat(userController.getUserFriends(1L)).contains(userTwo);
    }

    @Test
    public void removeFriend() {
        userController.addUser(createNewUser());
        userController.addFriend(1L, 2L);

        userController.removeFriend(1L, 2L);

        assertThat(userController.getUserFriends(1L)).isEmpty();
    }

    @Test
    public void getAllFriends() {
        User friendOne = userController.addUser(createNewUser());
        User friendTwo = userController.addUser(createNewUser());

        userController.addFriend(1L, 2L);
        userController.addFriend(1L, 3L);

        assertThat(userController.getUserFriends(1L)).contains(friendOne, friendTwo);
    }

    @Test
    public void getMutualFriends() {
        userController.addUser(createNewUser());
        User mutualFriend = userController.addUser(createNewUser());

        userController.addFriend(1L, 3L);
        userController.addFriend(2L, 3L);

        assertThat(userController.getMutualFriends(1L, 2L)).contains(mutualFriend);
    }


    // Film tests
    @Test
    public void getFilmById() {
        Optional<Film> filmOptional = Optional.of(filmController.getFilm(1L));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void addFilm() {
        filmController.addFilm(createNewFilm());

        assertThat(Optional.of(filmController.getFilm(2L)))
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 2L)
                );
    }

    @Test
    public void updateFilm() {
        Film updatedFilm = Film.builder()
                .name("updated film")
                .description("updated description")
                .duration(200)
                .releaseDate(LocalDate.of(1999, 11, 1))
                .mpa(ratingController.getMpa(2L))
                .genres(List.of(genreController.getGenre(2L)))
                .id(1L)
                .build();

        Optional<Film> actual = Optional.of(filmController.updateFilm(updatedFilm));

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("name", "updated film")
                                .hasFieldOrPropertyWithValue("description", "updated description")
                                .hasFieldOrPropertyWithValue("duration", 200)
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1999, 11, 1))
                                .hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    public void updateInvalidFilm() {
        assertThrows(ValidationException.class, () -> filmController.updateFilm(Film.builder()
                .name("updated film")
                .description("updated description")
                .duration(200)
                .releaseDate(LocalDate.of(1000, 11, 1))
                .mpa(ratingController.getMpa(2L))
                .genres(List.of(genreController.getGenre(2L)))
                .id(1L)
                .build()), "wrong release date");

        assertThrows(ValidationException.class, () -> filmController.updateFilm(Film.builder()
                .name("")
                .description("updated description")
                .duration(200)
                .releaseDate(LocalDate.of(1999, 11, 1))
                .mpa(ratingController.getMpa(2L))
                .genres(List.of(genreController.getGenre(2L)))
                .id(1L)
                .build()), "empty name");

        assertThrows(NotFoundException.class, () -> filmController.updateFilm(Film.builder()
                .name("updated film")
                .description("updated description")
                .duration(200)
                .releaseDate(LocalDate.of(1999, 11, 1))
                .mpa(ratingController.getMpa(9999L))
                .genres(List.of(genreController.getGenre(2L)))
                .id(1L)
                .build()), "unknown rating");

        assertThrows(NotFoundException.class, () -> filmController.updateFilm(Film.builder()
                .name("updated film")
                .description("updated description")
                .duration(200)
                .releaseDate(LocalDate.of(1999, 11, 1))
                .mpa(ratingController.getMpa(2L))
                .genres(List.of(genreController.getGenre(9999L)))
                .id(1L)
                .build()), "unknown genre");
    }

    @Test
    public void addLike() {
        filmController.addLike(1L, 1L);

        assertThat(filmController.getFilmLikes(1L)).contains(1L);
    }

    @Test
    public void removeLike() {
        filmController.addLike(1L, 1L);

        filmController.removeLike(1L, 1L);

        assertThat(filmController.getFilmLikes(1L)).isEmpty();
    }

    @Test
    public void getAllFilms() {
        Film testFilm = filmController.addFilm(createNewFilm());

        List<Film> actual = filmController.getFilms();

        assertThat(actual).isNotEmpty();
        assertThat(actual).contains(testFilm);
    }

    @Test
    public void getPopularFilms() {
        Film actual = filmController.addFilm(createNewFilm());
        filmController.addLike(2L, 1L);

        assertEquals(filmController.getPopularFilmGenreIdYear(1, 0, 0).size(), 1, "size popular films");
        assertEquals(filmController.getPopularFilmGenreIdYear(10, 0, 0).get(0), actual, "most popular film");
    }

    @Test
    public void updateUnknownFilm() {
        assertThrows(NotFoundException.class, () -> filmController.updateFilm(Film.builder()
                .name("updated film")
                .description("updated description")
                .duration(200)
                .releaseDate(LocalDate.of(1999, 11, 1))
                .mpa(ratingController.getMpa(2L))
                .genres(List.of(genreController.getGenre(2L)))
                .id(9999L)
                .build()), "unknown film");
    }

    // genres test
    @Test
    public void getAllGenres() {
        List<Genre> genres = genreController.getAllGenres();

        assertThat(genres).isNotEmpty();
        assertThat(genres).contains(genreController.getGenre(1L));
    }

    // mpa ratings test
    @Test
    public void getAllMPARatings() {
        List<Rating> ratings = ratingController.getAllMpa();

        assertThat(ratings).isNotEmpty();
        assertThat(ratings).contains(ratingController.getMpa(1L));
    }


    private Film createNewFilm() {
        return Film.builder()
                .name("new film")
                .description("new description")
                .duration(100)
                .releaseDate(LocalDate.of(2000, 11, 1))
                .mpa(ratingController.getMpa(1L))
                .genres(List.of(genreController.getGenre(1L)))
                .build();
    }

    private User createNewUser() {
        return User.builder()
                .name("user1")
                .login("login1")
                .email("email1@ya.ru")
                .birthday(LocalDate.of(2000, 10, 10))
                .build();
    }


}