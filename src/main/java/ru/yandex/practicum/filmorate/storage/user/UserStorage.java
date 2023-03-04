package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    User add(User user);

    List<User> getAll();

    User get(long id);

    User update(User user);

    void deleteUser(Long userId);

    User addFriend(User user, long friendId);

    List<User> getFriends(User user);

    User removeFriend(User user, long friendId);

    void deleteAll();

    Collection<Event> getEventFeed(User user);

    void addEvent(Long userId, String type, String operation, Long entityId);



}
