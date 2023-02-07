package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    UserStorage userStorage;

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
}
