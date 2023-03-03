package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Event {
    private Long timestamp;
    private Long userId;
    private String eventType;
    private String operation;
    private Long eventId;
    private Long entityId;
}



