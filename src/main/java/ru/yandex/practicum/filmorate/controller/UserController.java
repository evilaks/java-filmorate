package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();
    private int userId = 0;

    @GetMapping
    public List<User> getUsers() {
        return users.values().stream().collect(Collectors.toList());
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        if (users.containsKey(user.getId())) {
            throw new BadRequestException("User with such id already exist");
        } else if (!isValidUser(user)) {
            throw new ValidationException("Invalid user properties");
        } else {
            userId++;
            user.setId(userId);
            users.put(userId, user.normalize());
        }
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (!isValidUser(user)) {
            throw new ValidationException("Invalid user properties");
        } else if (users.containsKey(user.getId())) {
            users.put(user.getId(), user.normalize());
        } else {
            throw new NotFoundException("User with such id not found");
        }
        return user;
    }

    private boolean isValidUser(User user) {
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) return false;
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) return false;
        if (user.getBirthday().isAfter(LocalDate.now())) return false;
        return true;
    }

}
