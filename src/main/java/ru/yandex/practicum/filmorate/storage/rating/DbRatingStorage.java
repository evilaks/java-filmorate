package ru.yandex.practicum.filmorate.storage.rating;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DbRatingStorage implements RatingStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Rating get(int id) {
        log.debug("Extracting mpa rating with id={} from the database", id);
        String sql = "SELECT * FROM RATING WHERE ID=?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createMpa(rs), id).stream().findFirst().orElse(null);
    }

    @Override
    public List<Rating> getAll() {
        log.debug("Extracting all mpa ratings from the database");
        String sql = "SELECT * FROM RATING";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createMpa(rs));
    }

    private Rating createMpa(ResultSet rs) throws SQLException {
        return Rating.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }
}
