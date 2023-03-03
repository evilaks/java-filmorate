package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
@Primary
public class DbReviewStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

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
        userStorage.addEvent(review.getUserId(), "REVIEW", "ADD", review.getUserId());
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
        userStorage.addEvent(review.getUserId(), "REVIEW", "UPDATE", review.getUserId());

        return review;
    }

    @Override
    public Review get(Long id) {
        log.debug("Extracting a review from the database with id={}", id);
        String sql = "SELECT R.ID," +
                "R.CONTENT ," +
                "R.IS_POSITIVE ," +
                "R.USER_ID ," +
                "R.FILM_ID ," +
                "COALESCE(POSITIVE, 0) - COALESCE(NEGATIVE, 0) AS USEFUL FROM REVIEWS R " +
                "LEFT JOIN (" +
                "SELECT REVIEW_ID AS PID, count(user_id) AS POSITIVE " +
                "FROM REVIEW_MARKS rm WHERE IS_USEFUL = TRUE " +
                "GROUP BY PID) AS POS ON POS.PID = R.ID " +
                "LEFT JOIN (" +
                "SELECT REVIEW_ID AS NID, count(user_id) AS NEGATIVE " +
                "FROM REVIEW_MARKS rm WHERE IS_USEFUL = FALSE " +
                "GROUP BY NID" +
                ") AS NEG ON NEG.NID = R.ID " +
                "WHERE R.ID = ?";

        Review review = jdbcTemplate.query(sql, (rs, rowNum) -> createReview(rs), id)
                .stream()
                .findFirst()
                .orElse(null);

        return review;
    }

    @Override
    public List<Review> getAll(Integer count) {
        log.debug("Extracting all reviews from the database");
        String sql = "SELECT R.ID," +
                "R.CONTENT ," +
                "R.IS_POSITIVE ," +
                "R.USER_ID ," +
                "R.FILM_ID ," +
                "COALESCE(POSITIVE, 0) - COALESCE(NEGATIVE, 0) AS USEFUL FROM REVIEWS R " +
                "LEFT JOIN (" +
                "SELECT REVIEW_ID AS PID, count(user_id) AS POSITIVE " +
                "FROM REVIEW_MARKS rm WHERE IS_USEFUL = TRUE " +
                "GROUP BY PID) AS POS ON POS.PID = R.ID " +
                "LEFT JOIN (" +
                "SELECT REVIEW_ID AS NID, count(user_id) AS NEGATIVE " +
                "FROM REVIEW_MARKS rm WHERE IS_USEFUL = FALSE " +
                "GROUP BY NID" +
                ") AS NEG ON NEG.NID = R.ID " +
                "ORDER BY USEFUL DESC, R.ID " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createReview(rs), count);
    }

    @Override
    public List<Review> findReviewsByFilmId(Integer count, Long filmId) {
        log.debug("Extracting all reviews from the database for the film with id={}", filmId);
        String sql = "SELECT R.ID," +
                "R.CONTENT ," +
                "R.IS_POSITIVE ," +
                "R.USER_ID ," +
                "R.FILM_ID ," +
                "COALESCE(POSITIVE, 0) - COALESCE(NEGATIVE, 0) AS USEFUL FROM REVIEWS R " +
                "LEFT JOIN (" +
                "SELECT REVIEW_ID AS PID, count(user_id) AS POSITIVE " +
                "FROM REVIEW_MARKS rm WHERE IS_USEFUL = TRUE " +
                "GROUP BY PID) AS POS ON POS.PID = R.ID " +
                "LEFT JOIN (" +
                "SELECT REVIEW_ID AS NID, count(user_id) AS NEGATIVE " +
                "FROM REVIEW_MARKS rm WHERE IS_USEFUL = FALSE " +
                "GROUP BY NID" +
                ") AS NEG ON NEG.NID = R.ID " +
                "WHERE R.FILM_ID =? " +
                "ORDER BY USEFUL DESC, R.ID " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createReview(rs), filmId, count);
    }

    @Override
    public void addMark(Review review, Long userId, Boolean isUseful) {
        log.debug("Adding mark to review with id=" + review.getReviewId() + " from user with id= " + userId);
        String sql = "INSERT INTO REVIEW_MARKS (REVIEW_ID, USER_ID, IS_USEFUL) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, review.getReviewId(), userId, isUseful);
    }

    @Override
    public void removeMark(Review review, Long userId, Boolean isUseful) {
        log.debug("Deleting mark of review with id=" + review.getReviewId() + " from user with id= " + userId);
        String sql = "DELETE FROM REVIEW_MARKS WHERE REVIEW_ID=? AND USER_ID=? AND IS_USEFUL=?";
        jdbcTemplate.update(sql, review.getReviewId(), userId, isUseful);
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting a review from the database with id={}", id);
        userStorage.addEvent(get(id).getUserId(), "REVIEW", "REMOVE", get(id).getFilmId());

        String deleteReviewMarksQuery = "DELETE FROM REVIEW_MARKS WHERE REVIEW_ID=?";
        jdbcTemplate.update(deleteReviewMarksQuery, id);

        String deleteReviewQuery = "DELETE FROM REVIEWS WHERE ID=?";
        jdbcTemplate.update(deleteReviewQuery, id);
    }

    private Review createReview(ResultSet rs) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .useful(rs.getLong("useful"))
                .build();
    }


}
