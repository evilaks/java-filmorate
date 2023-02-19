package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DbGenreStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre get(Long id) {
        log.debug("Extracting genre with id={} from the database", id);
        String sql = "SELECT * FROM GENRE WHERE ID=?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createGenre(rs), id).stream().findFirst().orElse(null);
    }

    @Override
    public List<Genre> getAll() {
        log.debug("Extracting all genres from the database");
        String sql = "SELECT * FROM GENRE ORDER BY ID";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createGenre(rs));
    }

    private Genre createGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }
}
