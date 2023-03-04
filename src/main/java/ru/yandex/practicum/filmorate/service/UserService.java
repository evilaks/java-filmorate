package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User addUser(User user) {
        if (isInvalidUser(user)) {
            throw new ValidationException("Invalid user properties");
        } else {
            user = user.normalize();
            return userStorage.add(user);
        }
    }

    public User getUser(Long userId) {
        if (userStorage.get(userId) == null) {
            throw new NotFoundException("User with id= " + userId + " not found");
        }
        return userStorage.get(userId);
    }

    public User updateUser(User user) {
        if (isInvalidUser(user)) {
            throw new ValidationException("Invalid user properties");
        } else if (userStorage.get(user.getId()) != null) {
            user = user.normalize();
            userStorage.update(user);
        } else {
            throw new NotFoundException("User with such id not found");
        }
        return user;
    }

    public void deleteUser(Long userId) {
        Optional.ofNullable(userStorage.get(userId))
                .orElseThrow(() -> new NotFoundException("User for userId " + userId + " not found!"));
        userStorage.deleteUser(userId);
    }

    public User addFriend(User user, long friendId) {
        if (userStorage.get(user.getId()) == null) {
            throw new NotFoundException("User not found");
        } else if (userStorage.get(friendId) == null) {
            throw new NotFoundException("Friend not found");
        } else {
            userStorage.addEvent(user.getId(), "FRIEND", "ADD", friendId);
            return userStorage.addFriend(user, friendId);
        }
    }

    public User removeFriend(User user, long friendId) {
        if (userStorage.get(user.getId()) == null) {
            throw new NotFoundException("User not found");
        } else if (userStorage.get(friendId) == null) {
            throw new NotFoundException("Friend not found");
        } else {
            userStorage.addEvent(user.getId(), "FRIEND", "REMOVE", friendId);
            return userStorage.removeFriend(user, friendId);
        }
    }

    public List<User> getFreindsList(User user) {
        if (userStorage.get(user.getId()) == null) {
            throw new NotFoundException("User not found");
        } else {
            return userStorage.getFriends(user);
        }
    }

    public List<User> getMutualFriends(User actualUser, User targetUser) {
        Set<Long> actualUserFriends = userStorage.getFriends(actualUser)
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        Set<Long> targetUserFriends = userStorage.getFriends(targetUser)
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        Set<Long> mutualFriendsIds = actualUserFriends.stream()
                .filter(targetUserFriends::contains)
                .collect(Collectors.toSet());
        List<User> mutualFriends = new ArrayList<>();
        for (Long id : mutualFriendsIds) {
            mutualFriends.add(userStorage.get(id));
        }
        return mutualFriends;
    }

    private boolean isInvalidUser(User user) {
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.debug("Invalid User-object: login is blank");
            return true;
        }
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.debug("Invalid User-object: email is invalid");
            return true;
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Invalid User-object: birthday is from future");
            return true;
        }
        return false;
    }

    public Collection<Event> getEventFeed(User user) {
        return Optional.ofNullable(userStorage.getEventFeed(user))
                .orElseThrow(() -> new NotFoundException("Events for userId " + user.getId() + " not found!"));
    }
}
