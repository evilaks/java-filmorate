package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GenreController {
    private final GenreStorage genreStorage;
    private final GenreService genreService;

    @GetMapping("/genres")
    public List<Genre> getAllMpa() {
        return genreStorage.getAll();
    }

    @GetMapping("/genres/{id}")
    public Genre getMpa(@PathVariable("id") int genreId) {
        return genreService.getGenre(genreId);
    }
}
