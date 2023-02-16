package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

    public User addFriend(User user, long friendId) {
        User friend = userStorage.get(friendId);
        friend.addFriend(user.getId());
        userStorage.update(friend);
        return userStorage.update(user.addFriend(friendId));
    }

    public User removeFriend(User user, long friendId) {
        User friend = userStorage.get(friendId);
        friend.removeFriend(user.getId());
        userStorage.update(friend);
        return userStorage.update(user.removeFriend(friendId));
    }

    public List<User> getFreindsList(User user) {
        List<User> friendsList = new ArrayList<>();
        Set<Long> friendsIds = user.getFriends();
        for (Long id : friendsIds) {
            friendsList.add(userStorage.get(id));
        }
        return friendsList;
    }

    public List<User> getMutualFriends(User actualUser, User targetUser) {
        Set<Long> actualUserFriends = actualUser.getFriends();
        Set<Long> targetUserFriends = targetUser.getFriends();
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
}
