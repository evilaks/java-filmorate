package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DbDirectorStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> getAll() {
        return null;
    }

    @Override
    public Director getDirector() {
        return null;
    }

    @Override
    public Director addDirector(Director director) {
        return null;
    }

    @Override
    public Director updateDirector(Director director) {
        return null;
    }

    @Override
    public void deleteDirector(Long directorId) {

    }
}
