package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

@Slf4j
@Repository
@RequiredArgsConstructor
@Primary
public class DbReviewStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review add(Review review) {
        log.debug("Inserting new review into the database");
        return null;
    }

    @Override
    public Review update(Review review) {
        log.debug("Updating a review in the database with id={}", review.getReviewId());
        return null;
    }

    @Override
    public Review get(Long id) {
        log.debug("Extracting a review from the database with id={}", id);
        return null;
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting a review from the database with id={}", id);
    }
}
