package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
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
   /* {
        "timestamp": 123344556,
            "userId": 123,
            "eventType": "LIKE", // одно из значениий LIKE, REVIEW или FRIEND
            "operation": "REMOVE", // одно из значениий REMOVE, ADD, UPDATE
            "eventId": 1234, //primary key
            "entityId": 1234   // идентификатор сущности, с которой произошло событие
    }*/


