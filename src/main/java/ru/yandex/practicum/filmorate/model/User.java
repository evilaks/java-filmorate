package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class User {
    private Long id;
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    private LocalDate birthday;
    @Singular
    private Set<Long> friends;

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
            throw new NotFoundException("Friend not found");
        }
        return this;
    }

}
