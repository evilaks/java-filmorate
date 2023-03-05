package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    public List<Director> getAll() {
        return directorStorage.getAll();
    }

    public Director getDirector(Long directorId) {
        Director director = directorStorage.getDirector(directorId);
        if (director == null) {
            throw new NotFoundException("Director with id=" + directorId + " not found");
        } else return director;
    }

    public Director addDirector(Director director) {
        if (director.getName().isBlank()) {
            throw new ValidationException("Invalid director properties");
        } else return directorStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        if (director.getName().isBlank()) {
            throw new ValidationException("Invalid director properties");
        } else if (directorStorage.getDirector(director.getId()) == null) {
            throw new NotFoundException("Director with such id not found");
        } else return directorStorage.updateDirector(director);
    }

    public void deleteDirector(Long directorId) {
        if (directorStorage.getDirector(directorId) == null) {
            throw new NotFoundException("Director not found");
        } else {
            directorStorage.deleteDirector(directorId);
        }
    }
}
