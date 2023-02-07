package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exception.FriendNotFoundException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id;
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();

    public User normalize() {
        if (this.name == null || this.name.isBlank()) this.name = this.login;
        return this;
    }

    public User addFriend(long friendId) {
        this.friends.add(friendId);
        return this;
    }

    public User removeFriend(long friendId) {
        if (friends.contains(friendId)) {
            friends.remove(friendId);
        } else {
            throw new FriendNotFoundException("Friend not found");
        }
        return this;
    }
}
