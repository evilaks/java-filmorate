package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
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
        return jdbcTemplate.query(sql, (rs, rowNum) -> createUser(rs), id).stream().findFirst().orElse(null);
    }

    @Override
    public User update(User user) {
        log.debug("Updating user with id={} in the database", user.getId());

        String sqlQuery = "UPDATE USERS SET " +
                "NAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
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

    private User createUser(ResultSet rs) throws SQLException {

        return User.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .login(rs.getString("login"))
                .email(rs.getString("email"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }
}
