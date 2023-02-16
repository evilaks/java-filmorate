package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
@Primary
public class DbFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final RatingStorage ratingStorage;
    private final GenreStorage genreStorage;

    @Override
    public Film add(Film film) {
        log.debug("Inserting new film into the database");

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", film.getName())
                .addValue("description", film.getDescription())
                .addValue("release_date", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("rating_id", film.getMpa().getId());

        Long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        film.setId(id);

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
                jdbcTemplate.update(sql, film.getId(), genre.getId());
            }
        }

        return this.get(film.getId());
    }

    @Override
    public List<Film> getAll() {
        log.debug("Extracting all films from the database");
        String sql = "SELECT id FROM FILMS ORDER BY id DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> this.get(rs.getLong("id")));
    }

    @Override
    public Film get(long id) {
        log.debug("Extracting film with id={} from the database", id);
        String sql = "SELECT * FROM FILMS WHERE ID=?";
        Film film = jdbcTemplate.query(sql, (rs, rowNum) -> createFilm(rs), id)
                .stream()
                .findFirst()
                .orElse(null);

        if (film != null) {
            String requestGenres = "SELECT genre_id FROM film_genre WHERE film_id=?";
            List<Genre> filmGenres = new ArrayList<>(jdbcTemplate.query(requestGenres, (rs, rowNum) -> extractGenre(rs), id));
            film.setGenres(filmGenres);
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        log.debug("Updating film with id={} in the database", film.getId());
        String sql = "UPDATE FILMS SET " +
                "title=?, description=?, duration=?, release_date=?, rating_id=? " +
                "WHERE id=?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                film.getId());

        if (film.getGenres() != null) {
            String deleteQuery = "DELETE FROM film_genre WHERE film_id=?";
            jdbcTemplate.update(deleteQuery, film.getId());
            for (Genre genre : film.getGenres()) {
                String insertQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
                jdbcTemplate.update(insertQuery, film.getId(), genre.getId());
            }
        }
        return this.get(film.getId());
    }

    @Override
    public void remove(Film film) {
        log.debug("Deleting film with id={} from the database [NOT IMPLEMENTED]", film.getId());
    }

    private Film createFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("title"))
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .mpa(ratingStorage.get(rs.getInt("rating_id")))
                .build();
    }

    private Genre extractGenre(ResultSet rs) throws SQLException {
        return genreStorage.get(rs.getInt("genre_id"));
    }
}