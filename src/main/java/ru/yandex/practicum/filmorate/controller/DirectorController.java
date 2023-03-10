package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RequestMapping("/directors")
@RestController
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;


    @GetMapping
    public List<Director> getAll() {
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable("id") Long directorId) {
        return directorService.getDirector(directorId);
    }

    @PostMapping
    public Director addDirector(@RequestBody Director director) {
        return directorService.addDirector(director);
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director) {
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable("id") Long directorId) {
        directorService.deleteDirector(directorId);
    }
}
