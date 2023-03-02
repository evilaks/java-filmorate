package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Long, User> users = new HashMap<>();
    private long userId = 0;

    @Override
    public User add(User user) {
        userId++;
        user.setId(userId);
        users.put(userId, user);
        log.debug("Created user record with User-object {}", user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User get(long id) {
        return users.getOrDefault(id, null);
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        log.debug("Updated user record with id {}", user.getId());
        return user;
    }

    @Override
    public void remove(User user) {
        log.debug("Unimplemented \"remove\" method called");
    }

    @Override
    public User addFriend(User user, long friendId) {
        log.debug("Method \"addFriend\" not implemented in InMemoryUserStorage");
        return null;
    }

    @Override
    public List<User> getFriends(User user) {
        log.debug("Method \"getFriends\" not implemented in InMemoryUserStorage");
        return null;
    }

    @Override
    public User removeFriend(User user, long friendId) {
        log.debug("Method \"removeFriend\" not implemented in InMemoryUserStorage");
        return null;
    }

    @Override
    public void deleteAll() {
        log.debug("Method \"deleteAll\" not implemented in InMemoryUserStorage");
    }

    @Override
    public Collection<Event> getEventFeed(User user) {
        log.debug("Method \"getEventFeed\" not implemented in InMemoryUserStorage");
        return null;
    }

    @Override
    public void addEvent(Long userId, String type, String operation, Long entityId) {
        log.debug("Method \"addEvent\" not implemented in InMemoryUserStorage");
    }

    @Override
    public void addEntity(Object typeEntity) {
        log.debug("Method \"addEntity\" not implemented in InMemoryUserStorage");
    }
}
