package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Repository
@RequiredArgsConstructor
@Primary
public class DbReviewStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review add(Review review) {
        log.debug("Inserting new review into the database");

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("id");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("content", review.getContent())
                .addValue("is_positive", review.getIsPositive())
                .addValue("user_id", review.getUserId())
                .addValue("film_id", review.getFilmId());

        Long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        review.setReviewId(id);

        return review;
    }

    @Override
    public Review update(Review review) {
        log.debug("Updating a review in the database with id={}", review.getReviewId());

        String sql = "UPDATE REVIEWS SET " +
                "CONTENT=?, IS_POSITIVE=?, USER_ID=?, FILM_ID=? " +
                "WHERE id=?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getReviewId());

        return review;
    }

    @Override
    public Review get(Long id) {
        log.debug("Extracting a review from the database with id={}", id);
        String sql = "SELECT * FROM REVIEWS WHERE ID=?";
        Review review = jdbcTemplate.query(sql, (rs, rowNum) -> createReview(rs), id)
                .stream()
                .findFirst()
                .orElse(null);

        // todo count useful

        return review;
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting a review from the database with id={}", id);
        String deleteQuery = "DELETE FROM REVIEWS WHERE ID=?";
        jdbcTemplate.update(deleteQuery, id);

        // todo delete review marks
    }

    private Review createReview(ResultSet rs) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .build();
    }
}
