package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private UserStorage userStorage;

    @GetMapping
    public List<User> getUsers() {
        return userStorage.getAll();
    }

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        log.debug("Received POST-request at /users endpoint with User object: {}", user.toString());
        return userStorage.add(user);
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        log.debug("Received PUT-request at /users endpoint with User-object {}", user.toString());
        return userStorage.update(user);
    }

}
