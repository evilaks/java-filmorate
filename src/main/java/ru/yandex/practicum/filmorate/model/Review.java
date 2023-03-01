package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Review {
    private Long reviewId;
    private String content;
    private boolean isPositive;
    private Long userId;
    private Long filmId;
    private Long useful;
}
