package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findFilms() {
        List<Film> films = filmService.findAll();
        log.info("Получен запрос GET /films. Количество фильмов сейчас: {}", films.size());
        return films;
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable Long id) {
        log.info("Получен запрос GET /films/{}", id);
        return filmService.findById(id);
    }

    @PostMapping
    public Film createFilms(@Valid @RequestBody Film film) {
        log.info("Попытка создать фильм: {}", film);
        Film created = filmService.create(film);
        log.info("Фильм успешно создан: id={}, name='{}'", created.getId(), created.getName());
        return created;
    }

    @PutMapping
    public Film updateFilms(@Valid @RequestBody Film film) {
        log.info("Попытка обновить фильм: {}", film);
        Film updated = filmService.update(film);
        log.info("Фильм успешно обновлён: id={}, name='{}'", updated.getId(), updated.getName());
        return updated;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь {} ставит лайк фильму {}", userId, id);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь {} удаляет лайк фильма {}", userId, id);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос на популярные фильмы: count={}", count);
        return filmService.getPopular(count);
    }
}