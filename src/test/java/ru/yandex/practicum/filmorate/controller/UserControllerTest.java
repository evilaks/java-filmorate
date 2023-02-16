package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

public class UserControllerTest {

    private User testUser;
    private UserController testUserController;

    @BeforeEach
    void beforeEach() {
        testUser = User.builder()
                .name("username")
                .login("userlogin")
                .email("mail@example.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        UserStorage testUserStorage = new InMemoryUserStorage();
        UserService testUserService = new UserService(testUserStorage);
        testUserController = new UserController(testUserStorage, testUserService);
    }

    @Test
    void addUser() {
        User actual = testUserController.addUser(testUser);
        assertEquals(1, actual.getId(), "User object doesn't get id from userController");

        // name validation test
        testUser.setId(0L);
        testUser.setName("");
        actual = testUserController.addUser(testUser);
        assertEquals("userlogin", actual.getName(), "empty username");

        // login validation test
        testUser.setId(0L);
        testUser.setName("username");
        testUser.setLogin("");
        assertThrows(ValidationException.class, () -> testUserController.addUser(testUser), "empty login");

        testUser.setLogin("user login");
        assertThrows(ValidationException.class, () -> testUserController.addUser(testUser), "login with space");

        // email validation test
        testUser.setLogin("userlogin");
        testUser.setEmail("");
        assertThrows(ValidationException.class, () -> testUserController.addUser(testUser), "empty email");

        testUser.setEmail("email.example");
        assertThrows(ValidationException.class, () -> testUserController.addUser(testUser), "wrong email");

        // birthday validation test
        testUser.setEmail("mail@example.com");
        testUser.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> testUserController.addUser(testUser), "wrong birthday");

    }

    @Test
    void updateUser() {
        // success test
        User actualUser = testUserController.addUser(testUser);

        testUser.setId(1L);
        assertEquals(actualUser, testUser, "Added film objects doesn't match");

        testUser.setName("newname");
        testUser.setLogin("newlogin");
        testUser.setEmail("newmail@example.com");
        testUser.setBirthday(LocalDate.of(2000, 12, 30));

        User actual = testUserController.updateUser(testUser);
        assertEquals(testUser, actual, "Updated user objects doesn't match");

        // unknown id
        actual.setId(2L);
        assertThrows(NotFoundException.class, () -> testUserController.updateUser(actual), "unknown id");

        // name validation test
        actual.setId(1L);
        actual.setName("");
        String actualName = testUserController.updateUser(actual).getName();
        assertEquals(actual.getLogin(), actualName, "empty username");

        // login validation test
        actual.setName("username");
        actual.setLogin("");
        assertThrows(ValidationException.class, () -> testUserController.updateUser(actual), "empty login");

        actual.setLogin("user login");
        assertThrows(ValidationException.class, () -> testUserController.updateUser(actual), "login with spaces");

        // email validation test
        actual.setLogin("newlogin");
        actual.setEmail("");
        assertThrows(ValidationException.class, () -> testUserController.updateUser(actual), "empty email");

        actual.setEmail("mail.example");
        assertThrows(ValidationException.class, () -> testUserController.updateUser(actual), "wrong email");

        // birthday validation test
        actual.setEmail("mail@example.com");
        actual.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> testUserController.updateUser(actual), "wrong bithday");
    }

    @Test
    void getUsers() {
        testUserController.addUser(testUser);

        assertEquals(1, testUserController.getUsers().size(), "Wrong size of returning users array");

        testUser.setId(1L);
        User actual = testUserController.getUsers().get(0);
        assertEquals(testUser, actual, "Returning user doesn't match added one");
    }
}
