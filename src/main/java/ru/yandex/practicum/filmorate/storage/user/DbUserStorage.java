package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.sql.*;

@Slf4j
@Repository
@RequiredArgsConstructor
@Primary
public class DbUserStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        log.debug("Inserting new user into the database");

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", user.getName())
                .addValue("login", user.getLogin())
                .addValue("email", user.getEmail())
                .addValue("birthday", user.getBirthday());

        Long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        user.setId(id);
        addEntity(user);
        return user;
    }

    @Override
    public List<User> getAll() {
        log.debug("Extracting all users from the database");
        String sql = "SELECT * FROM USERS";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createUser(rs));
    }

    @Override
    public User get(long id) {
        log.debug("Extracting user with id={} from the database", id);
        String sql = "SELECT * FROM USERS WHERE ID=?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createUser(rs), id)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public User update(User user) {
        log.debug("Updating user with id={} in the database", user.getId());

        String sql = "UPDATE USERS SET " +
                "NAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());

        return user;
    }

    @Override
    public void remove(User user) {
        log.debug("Deleting user with id={} from the database [NOT IMPLEMENTED]", user.getId());
    }

    @Override
    public User addFriend(User user, long friendId) {
        log.debug("Adding a new friend with id={} to the user with id={}", friendId, user.getId());

        String sql = "INSERT INTO FRIENDSHIP_REQUESTS (USER_ID, FRIEND_ID, IS_APPROVED)" +
                "VALUES (?, ?, ?)";

        jdbcTemplate.update(sql, user.getId(), friendId, Boolean.FALSE);
        addEvent(user.getId(), "FRIEND", "ADD", friendId);

        return user;
    }

    @Override
    public List<User> getFriends(User user) {
        log.debug("Extracting a friends list from the database for the user with id={}", user.getId());

        String sql = "SELECT * FROM USERS WHERE ID IN (SELECT FRIEND_ID FROM FRIENDSHIP_REQUESTS WHERE USER_ID=?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createUser(rs), user.getId());
    }

    @Override
    public User removeFriend(User user, long friendId) {
        log.debug("Removing a friend with id={} the from friendlist of a user with id={}", friendId, user.getId());
        String sql = "DELETE FROM FRIENDSHIP_REQUESTS WHERE USER_ID=? AND FRIEND_ID=?";
        jdbcTemplate.update(sql, user.getId(), friendId);
        addEvent(user.getId(), "FRIEND", "REMOVE", friendId);
        return user;
    }

    @Override
    public void deleteAll() {
        log.debug("Deleting all data from users");
        jdbcTemplate.update("DELETE FROM LIKES");
        jdbcTemplate.update("DELETE FROM FRIENDSHIP_REQUESTS");
        jdbcTemplate.update("delete from users");
        jdbcTemplate.update("ALTER TABLE USERS ALTER COLUMN ID RESTART WITH 1");
    }

    private User createUser(ResultSet rs) throws SQLException {

        return User.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .login(rs.getString("login"))
                .email(rs.getString("email"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    @Override
    public Collection<Event> getEventFeed(User user) {
        String sqlEvent = "SELECT * FROM EVENT_FEED WHERE USER_ID = " + user.getId();
        return jdbcTemplate.query(sqlEvent, (resultSet, rowNum) -> getEventDb(resultSet));

    }

    private Event getEventDb(ResultSet resultSet) throws SQLException {
        long eventId = resultSet.getInt("EVENT_ID");
        return new Event(
                resultSet.getTimestamp("TIME").toInstant().toEpochMilli(),
                resultSet.getLong("USER_ID"),
                resultSet.getString("EVENT_TYPE"),
                resultSet.getString("OPERATION"),
                eventId,
                resultSet.getLong("ENTITY_ID"));
    }

    private Entity getEntityDb(ResultSet resultSet) throws SQLException {
        return new Entity(
                resultSet.getInt("ID"),
                resultSet.getInt("FILM_ID"),
                resultSet.getInt("USER_ID"));
    }

    @Override
    public void addEvent(Long userId, String type, String operation, Long entityId) {
        log.debug("Inserting new event into the database");
        Entity entity;
        /*if (type.equals("FRIEND")) {
            String sqlEntity = "SELECT * FROM ENTITY WHERE USER_ID = " + entityId;
            entity = jdbcTemplate.queryForObject(sqlEntity, (resultSet, rowNum) -> getEntityDb(resultSet));
        } else {
            String sqlEntity = "SELECT * FROM ENTITY WHERE FILM_ID = " + entityId;
            entity = jdbcTemplate.queryForObject(sqlEntity, (resultSet, rowNum) -> getEntityDb(resultSet));
        }*/
    SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("EVENT_FEED")
            .usingGeneratedKeyColumns("EVENT_ID");

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("TIME", Instant.now())
            .addValue("USER_ID", userId)
            .addValue("EVENT_TYPE", type)
            .addValue("OPERATION", operation)
            .addValue("ENTITY_ID", entityId);
    Long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
}

    @Override
    public void addEntity(Object typeEntity) {
        log.debug("Inserting new entity into the database");
        if (typeEntity instanceof Film) {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("ENTITY")
                    .usingGeneratedKeyColumns("ID");
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("FILM_ID", ((Film) typeEntity).getId());
            Long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        } else if (typeEntity instanceof User) {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("ENTITY")
                    .usingGeneratedKeyColumns("ID");
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("USER_ID", ((User) typeEntity).getId());
            Long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        }
    }
}
