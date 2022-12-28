package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserControllerTest {

    User testUser;
    UserController testUserController;

    @BeforeEach
    void beforeEach() {
        testUser = new User();
        testUser.setName("username");
        testUser.setLogin("userlogin");
        testUser.setEmail("mail@example.com");
        testUser.setBirthday(LocalDate.of(2000, 1, 1));

        testUserController = new UserController();
    }

    @Test
    void addUser() {
        User actual = testUserController.addUser(testUser);
        assertEquals(1, actual.getId(), "User object doesn't get id from userController");

        // existing id
        testUser.setId(1);
        assertThrows(BadRequestException.class, () -> testUserController.addUser(testUser), "id already exist");

        // name validation test
        testUser.setId(0);
        testUser.setName("");
        actual = testUserController.addUser(testUser);
        assertEquals("userlogin", actual.getName(), "empty username");

        // login validation test
        testUser.setId(0);
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

        testUser.setId(1);
        assertEquals(actualUser, testUser, "Added film objects doesn't match");

        testUser.setName("newname");
        testUser.setLogin("newlogin");
        testUser.setEmail("newmail@example.com");
        testUser.setBirthday(LocalDate.of(2000, 12, 30));

        User actual = testUserController.updateUser(testUser);
        assertEquals(testUser, actual, "Updated user objects doesn't match");

        // unknown id
        actual.setId(2);
        assertThrows(NotFoundException.class, () -> testUserController.updateUser(actual), "unknown id");

        // name validation test
        actual.setId(1);
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
}