package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;

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

        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                String sql = "INSERT INTO film_director (film_id, director_id) VALUES (?, ?)";
                jdbcTemplate.update(sql, film.getId(), director.getId());
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

            String requestDirectors = "SELECT director_id FROM film_director WHERE film_id = ?";
            List<Director> filmDirectors = new ArrayList<>(jdbcTemplate.query(requestDirectors, (rs, rowNum)
                    -> extractDirector(rs), id));
            film.setDirectors(filmDirectors);
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

        String deleteQuery = "DELETE FROM film_director WHERE film_id = ?";
        jdbcTemplate.update(deleteQuery, film.getId());
        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                String insertQuery = "INSERT INTO film_director (film_id, director_id) VALUES (?, ?)";
                jdbcTemplate.update(insertQuery, film.getId(), director.getId());
            }
        }
        return this.get(film.getId());
    }

    @Override
    public void deleteFilm(Long filmId) {
        log.debug("Deleting a film with id={}", filmId);

        String sqlFilmDirector = "DELETE FROM FILM_DIRECTOR WHERE FILM_ID=?";
        jdbcTemplate.update(sqlFilmDirector, filmId);

        String sqlIdReview = "SELECT ID FROM REVIEWS WHERE FILM_ID = ?";
        List<Integer> reviews = jdbcTemplate.queryForList(sqlIdReview, Integer.class, filmId);
        String sqlReviewMark = "DELETE FROM REVIEW_MARKS WHERE REVIEW_ID=?";
        for (Integer id : reviews) {
            jdbcTemplate.update(sqlReviewMark, id);
        }

        String sqlReview = "DELETE FROM REVIEWS WHERE FILM_ID=?";
        jdbcTemplate.update(sqlReview, filmId);

        String sqlLikes = "DELETE FROM LIKES WHERE FILM_ID=?";
        jdbcTemplate.update(sqlLikes, filmId);

        String sqlGenre = "DELETE FROM FILM_GENRE WHERE FILM_ID=?";
        jdbcTemplate.update(sqlGenre, filmId);

        String sqlFilm = "DELETE FROM FILMS WHERE ID=?";
        jdbcTemplate.update(sqlFilm, filmId);
    }

    @Override
    public void deleteAll() {
        log.debug("Deleting all film data from the database");
        jdbcTemplate.update("DELETE from LIKES");
        jdbcTemplate.update("DELETE from FILM_GENRE");
        jdbcTemplate.update("DELETE from FILMS");
        jdbcTemplate.update("ALTER TABLE FILMS ALTER COLUMN ID RESTART WITH 1;");
    }

    @Override
    public void addLike(Film film, long userId) {
        log.debug("Adding like to film with id={} from user with id={}", film.getId(), userId);
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, film.getId(), userId);
    }

    @Override
    public List<Long> getLikes(Film film) {
        log.debug("Extracting likes of film with id={}", film.getId());
        String sql = "SELECT user_id FROM likes WHERE film_id=?";
        return new ArrayList<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), film.getId()));
    }

    @Override
    public void removeLike(Film film, long userId) {
        log.debug("Removing like from film with id={} from user with id={}", film.getId(), userId);
        String sql = "DELETE FROM likes WHERE film_id=? AND user_id=?";
        jdbcTemplate.update(sql, film.getId(), userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        log.debug("Extracting {} popular films from the database", count);
        String sql = "SELECT FILM_ID \n" +
                "FROM (\n" +
                "\tSELECT f.ID film_id, COUNT(l.USER_ID) likes_count \n" +
                "\tFROM FILMS f \n" +
                "\tLEFT JOIN LIKES l ON f.ID=l.FILM_ID \n" +
                "\tGROUP BY f.ID\n" +
                "\tORDER BY likes_count DESC) AS POPULAR\n" +
                "\tLIMIT ?;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> this.get(rs.getLong("film_id")), count);
    }

    @Override
    public List<Long> getFilmLikes(Film film) {
        log.debug("Extracting from the database users added likes to film with id={}", film.getId());
        String sql = "SELECT USER_ID FROM LIKES " +
                "WHERE FILM_ID=?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), film.getId());
    }

    @Override
    public List<Long> getIdFilmsWithUserLikes(Long userId) { // id фильмов которым поставил лайк этот user
        log.debug("Extracting from the database of films that the user has liked with id={}", userId);
        return jdbcTemplate.queryForList("SELECT FILM_ID FROM LIKES WHERE USER_ID=?", Long.class, userId);
    }

    @Override
    public List<Film> getSortedFilmsFromDirector(Long directorId, String sortBy) {
        String sql = "";
        switch (sortBy) {
            case "year":
                sql = "SELECT FILMS.ID AS FILM_ID\n" +
                        "FROM FILM_DIRECTOR\n" +
                        "LEFT JOIN FILMS ON FILM_DIRECTOR.FILM_ID = FILMS.ID\n" +
                        "WHERE FILM_DIRECTOR.DIRECTOR_ID = ?\n" +
                        "ORDER BY EXTRACT (YEAR FROM FILMS.RELEASE_DATE)";
                break;
            case "likes":
                sql = "SELECT FILM_DIRECTOR.FILM_ID\n" +
                        "FROM FILM_DIRECTOR\n" +
                        "LEFT JOIN LIKES ON FILM_DIRECTOR.FILM_ID = LIKES.FILM_ID\n" +
                        "WHERE FILM_DIRECTOR.DIRECTOR_ID = ?\n" +
                        "GROUP BY FILM_DIRECTOR.FILM_ID\n" +
                        "ORDER BY COUNT(LIKES.USER_ID) DESC";
        }
        ;
        return jdbcTemplate.query(sql, (rs, rowNum) -> this.get(rs.getLong("film_id")), directorId);
    }

    @Override
    public List<Long> getPopularFilmGenreIdYear(int year, int genreId, int count) {
        if(year == 0 && genreId == 0 ){
            log.debug("Extracting {} popular films from the database", count);
            String sql = "SELECT FILM_ID \n" +
                    "FROM (\n" +
                    "\tSELECT f.ID film_id, COUNT(l.USER_ID) likes_count \n" +
                    "\tFROM FILMS f \n" +
                    "\tLEFT JOIN LIKES l ON f.ID=l.FILM_ID \n" +
                    "\tGROUP BY f.ID\n" +
                    "\tORDER BY likes_count DESC) AS POPULAR\n" +
                    "\tLIMIT ?;";
            ArrayList<Long> idFilms = new ArrayList<>(jdbcTemplate.queryForList(sql, Long.class, count));
            return idFilms;
        }else if(genreId > 0 && year == 0){
            log.debug("Extracting {} popular films from the database", genreId);
            String sql = "SELECT ID \n" +
            "FROM FILMS AS fi \n" +
            "LEFT JOIN FILM_GENRE AS fg ON fi.id = fg.film_id \n" +
            "LEFT JOIN LIKES AS li ON fi.id = li.film_id \n" +
            "WHERE GENRE_ID = ? \n" +
            "GROUP BY ID \n" +
            "ORDER BY COUNT(GENRE_ID) DESC \n";
            ArrayList<Long> idFilms = new ArrayList<>(jdbcTemplate.queryForList(sql, Long.class, genreId));
            return idFilms;

        }else if(year > 0 && genreId == 0){
            log.debug("Extracting {} popular films from the database", year);
            String sql = "SELECT ID \n" +
                    "FROM FILMS AS fi \n" +
                    "LEFT JOIN FILM_GENRE AS fg ON fi.id = fg.film_id \n" +
                    "LEFT JOIN LIKES AS li ON fi.id = li.film_id \n" +
                    "WHERE EXTRACT(YEAR FROM RELEASE_DATE) = ? \n" +
                    "GROUP BY ID \n" +
                    "ORDER BY COUNT(GENRE_ID) DESC \n";
            ArrayList<Long> idFilms = new ArrayList<>(jdbcTemplate.queryForList(sql, Long.class, year));
            return idFilms;


        }else {
            log.debug("Extract from the database of popular films by genre and year genreId = "+ genreId + "year = " + year);
            String sql = "SELECT ID \n" +
                    "FROM (\n" +
                    "\tSELECT ID, COUNT(USER_ID) AS LIKES_COUNT \n" +
                    "\tFROM FILMS AS fi \n" +
                    "\tINNER JOIN FILM_GENRE AS fg ON fi.id = fg.film_id \n" +
                    "\tINNER JOIN LIKES AS li ON fi.id = li.film_id \n" +
                    "\tWHERE  EXTRACT(YEAR FROM RELEASE_DATE) = ? AND GENRE_ID = ? \n" +
                    "\tGROUP BY ID \n" +
                    "\tORDER BY LIKES_COUNT DESC) \n" +
                    "\tLIMIT ?";
            ArrayList<Long> idFilms = new ArrayList<>(jdbcTemplate.queryForList(sql, Long.class, year, genreId, count));
            return idFilms;
        }
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

    @Override
    public List<Film> getRecommendations(Long userId) {
        String sql = " SELECT * " +
                " FROM FILMS " +
                " WHERE ID IN (" +
                "    SELECT RESULT.BESTUSERSFILMS AS RECOMENDATION_FILM_ID " +
                "    FROM (SELECT IFNULL(USERSFILMS.FILM_ID, -1) AS USERSFILMS, BESTUSERSFILMS.FILM_ID AS BESTUSERSFILMS " +
                "          FROM (SELECT FILM_ID FROM LIKES WHERE USER_ID = ?) AS USERSFILMS " +
                "                   RIGHT JOIN (SELECT FILM_ID " +
                "                               FROM LIKES " +
                "                               WHERE USER_ID IN (" +
                "                                   SELECT BESTUSER.USER_ID " +
                "                                   FROM (SELECT L2.USER_ID, COUNT(L2.USER_ID) " +
                "                                         FROM (SELECT * FROM LIKES WHERE USER_ID = ?) AS L1 " +
                "                                                  LEFT JOIN LIKES AS L2 ON L1.FILM_ID = L2.FILM_ID " +
                "                                         WHERE L2.USER_ID <> ? " +
                "                                         GROUP BY L2.USER_ID " +
                "                                         ORDER BY COUNT(L2.USER_ID) DESC " +
                "                                         LIMIT 1) AS BESTUSER)) AS BESTUSERSFILMS " +
                "                              ON USERSFILMS.FILM_ID = BESTUSERSFILMS.FILM_ID) AS RESULT " +
                "    WHERE RESULT.USERSFILMS = -1)";
        return jdbcTemplate.query(sql,(rs,rowNum)->this.get(rs.getLong("ID")),userId,userId,userId);
    }

    private Genre extractGenre(ResultSet rs) throws SQLException {
        return genreStorage.get(rs.getLong("genre_id"));
    }

    private Director extractDirector(ResultSet rs) throws SQLException {
        return directorStorage.getDirector(rs.getLong("director_id"));
    }
}
