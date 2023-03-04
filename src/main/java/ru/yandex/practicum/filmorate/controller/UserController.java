package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @GetMapping
    public List<User> getUsers() {
        log.debug("Received GET-request at /users endpoint");
        return userStorage.getAll();
    }

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        log.debug("Received POST-request at /users endpoint with User object: {}", user.toString());
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        log.debug("Received PUT-request at /users endpoint with User-object {}", user.toString());
        return userService.updateUser(user);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable("userId") Long userId) {
        log.debug("Received GET-request at /users/{} endpoint", userId);
        return userService.getUser(userId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getUserFriends(@PathVariable("userId") Long userId) {
        log.debug("Received GET-request at /users/{}/friends endpoint", userId);
        return userService.getFreindsList(userService.getUser(userId));
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriend(@PathVariable("userId") Long userId, @PathVariable("friendId") Long friendId) {
        log.debug("Received PUT-request at /users/{}/friends/{} endpoint", userId, friendId);
        return userService.addFriend(userService.getUser(userId), friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public User removeFriend(@PathVariable("userId") Long userId, @PathVariable("friendId") Long friendId) {
        log.debug("Received DELETE-request at /users/{}/friends/{} endpoint", userId, friendId);
        return userService.removeFriend(userService.getUser(userId), friendId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable("userId") Long userId, @PathVariable("otherId") Long otherId) {
        log.debug("Received GET-request at /users/{}/friends/common/{} endpoint", userId, otherId);
        return userService.getMutualFriends(userService.getUser(userId), userService.getUser(otherId));
    }

    @GetMapping("/{id}/feed")
    public Collection<Event> getEventFeed(@PathVariable("id") Long userId) {
        log.debug("Received GET-request at /users/{}/feed endpoint", userId);
        return userService.getEventFeed(userService.getUser(userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.debug("Received DELETE-request at /users/{} endpoint", userId);
        userService.deleteUser(userId);
    }
}
