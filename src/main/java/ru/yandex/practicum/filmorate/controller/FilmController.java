package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService films;

    // Конструктор для Spring (DI)
    public FilmController(FilmService films) {
        this.films = films;
    }

    // Конструктор по умолчанию для юнит-тестов (new FilmController())
    public FilmController() {
        this.films = new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage());
    }

    public Film createFilms(Film film) {
        return films.create(film);
    }

    // --- CRUD ---
    @GetMapping
    public Collection<Film> findAll() {
        log.info("GET /films");
        return films.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable long id) {
        log.info("GET /films/{}", id);
        return films.get(id);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("POST /films body={}", film);
        return films.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("PUT /films body={}", film);
        return films.update(film);
    }

    // --- Лайки + Топ ---
    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable long id, @PathVariable long userId) {
        log.info("PUT /films/{}/like/{}", id, userId);
        films.like(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlike(@PathVariable long id, @PathVariable long userId) {
        log.info("DELETE /films/{}/like/{}", id, userId);
        films.unlike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> popular(@RequestParam(defaultValue = "10") int count) {
        log.info("GET /films/popular?count={}", count);
        return films.top(count);
    }
}
