package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
public class UserController {

    private UserStorage userStorage;
    private UserService userService;

    @GetMapping("/users")
    public List<User> getUsers() {
        return userStorage.getAll();
    }

    @PostMapping("/users")
    public User addUser(@RequestBody @Valid User user) {
        log.debug("Received POST-request at /users endpoint with User object: {}", user.toString());
        return userStorage.add(user);
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody @Valid User user) {
        log.debug("Received PUT-request at /users endpoint with User-object {}", user.toString());
        return userStorage.update(user);
    }

    @GetMapping("/users/{userId}")
    public User getUser(@PathVariable("userId") Long userId) {
        return userStorage.get(userId);
    }

    @GetMapping("/users/{userId}/friends")
    public List<User> getUserFriends(@PathVariable("userId") Long userId) {
        return userService.getFreindsList(userStorage.get(userId));
    }

    @PutMapping("/users/{userId}/friends/{friendId}")
    public User addFriend(@PathVariable("userId") Long userId, @PathVariable("friendId") Long friendId) {
        return userService.addFriend(userStorage.get(userId), friendId);
    }

    @DeleteMapping("/users/{userId}/friends/{friendId}")
    public User removeFriend(@PathVariable("userId") Long userId, @PathVariable("friendId") Long friendId) {
        return userService.removeFriend(userStorage.get(userId), friendId);
    }

    @GetMapping("/users/{userId}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable("userId") Long userId, @PathVariable("otherId") Long otherId) {
        return userService.getMutualFriends(userStorage.get(userId), userStorage.get(otherId));
    }

}
