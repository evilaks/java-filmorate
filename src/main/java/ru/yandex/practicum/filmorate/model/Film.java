package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class Film {
    private Long id;
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    private final Set<Long> likes = new HashSet<>();
    private Rating mpa;
    private List<Genre> genres;
    private List<Director> directors;

    public Film addLike(long userId) {
        likes.add(userId);
        return this;
    }

    public Film removeLike(long userId) {
        likes.remove(userId);
        return this;
    }

    public List<Long> getLikes() {
        return new ArrayList<>(likes);
    }
}
