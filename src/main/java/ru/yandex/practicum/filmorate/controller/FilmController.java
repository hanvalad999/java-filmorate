package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/films", produces = "application/json")
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Поступил запрос на добавление фильма.");
        return filmStorage.create(film);
    }

    @PutMapping
    public Film changeFilm(@Valid @RequestBody Film film) {
        log.info("Поступил запрос на изменения фильма.");
        return filmStorage.update(film);
    }

    // id — это id фильма, userId — id пользователя
    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable int id, @PathVariable int userId) {
        log.info("Поступил запрос на присвоение лайка фильму.");
        filmService.like(id, userId);
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Поступил запрос на получение списка всех фильмов.");
        return filmStorage.findAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        log.info("Получен GET-запрос на получение фильма");
        return filmStorage.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getBestFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Поступил запрос на получение списка популярных фильмов.");
        return filmService.getTopFilms(count);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Поступил запрос на удаление лайка у фильма.");
        filmService.deleteLike(id, userId);   // порядок: (filmId, userId)
    }
}
