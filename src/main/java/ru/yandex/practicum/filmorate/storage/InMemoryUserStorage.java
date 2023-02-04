package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Long, User> users = new HashMap<>();
    private long userId = 0;

    @Override
    public User add(User user) {
        if (users.containsKey(user.getId())) {
            log.debug("User with such id already exist");
            throw new BadRequestException("User with such id already exist");
        } else if (!isValidUser(user)) {
            throw new ValidationException("Invalid user properties");
        } else {
            userId++;
            user.setId(userId);
            user = user.normalize();
            users.put(userId, user);
            log.debug("Created user record with User-object {}", user);
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User get(long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else throw new NotFoundException("User not found");

    }

    @Override
    public User update(User user) {
        if (!isValidUser(user)) {
            throw new ValidationException("Invalid user properties");
        } else if (users.containsKey(user.getId())) {
            user = user.normalize();
            users.put(user.getId(), user);
            log.debug("Updated user record with id {}", user.getId());
        } else {
            log.debug("User with such id not found");
            throw new NotFoundException("User with such id not found");
        }
        return user;
    }

    @Override
    public void remove(User user) {
        log.debug("Unimplemented \"remove\" method called");
    }

    private boolean isValidUser(User user) {
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.debug("Invalid User-object: login is blank");
            return false;
        }
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.debug("Invalid User-object: email is invalid");
            return false;
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Invalid User-object: birthday is from future");
            return false;
        }
        return true;
    }
}
