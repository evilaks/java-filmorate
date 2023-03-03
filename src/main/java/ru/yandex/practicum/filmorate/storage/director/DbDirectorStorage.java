package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DbDirectorStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> getAll() {
        log.debug("Extracting all directors from the database");
        String sql = "SELECT * FROM DIRECTOR";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createDirector(rs));
    }

    @Override
    public Director getDirector(Long directorId) {
        log.debug("Extracting director with id={} from the database", directorId);
        String sql = "SELECT * FROM DIRECTOR WHERE ID=?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createDirector(rs), directorId)
                .stream().findFirst().orElse(null);
    }

    @Override
    public Director addDirector(Director director) {
        log.debug("Inserting new director into the database");
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("director")
                .usingGeneratedKeyColumns("id");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", director.getName());
        Long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        director.setId(id);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        log.debug("Updating director with id={} in the database", director.getId());
        String sql = "UPDATE DIRECTOR SET " +
                "NAME = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql,
                director.getName(),
                director.getId());
        return director;
    }

    @Override
    public void deleteDirector(Long directorId) {
        log.debug("Removing a director with id={}", directorId);
        String sql = "DELETE FROM FILM_DIRECTOR WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sql, directorId);
        sql = "DELETE FROM DIRECTOR WHERE ID = ?";
        jdbcTemplate.update(sql, directorId);
    }

    private Director createDirector(ResultSet rs) throws SQLException {

        return Director.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }
}
