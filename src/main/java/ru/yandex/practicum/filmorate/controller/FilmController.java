package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findFilms() {
        log.info("Получен запрос GET /films. Количество фильмов сейчас: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film createFilms(@RequestBody Film film) {
        log.info("Попытка создать фильм: {}", film);

        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Фильм успешно создан: id={}, name='{}'", film.getId(), film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilms(@RequestBody Film film) {
        log.info("Попытка обновить фильм: {}", film);

        if (film.getId() == null || !films.containsKey(film.getId())) {
            log.warn("Обновление не удалось: фильм с id={} не найден", film.getId());
            throw new ValidationException("Фильм с таким ID не найден для обновления");
        }

        films.put(film.getId(), film);

        log.info("Фильм успешно обновлён: id={}, name='{}'", film.getId(), film.getName());
        return film;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return currentMaxId + 1;
    }
}
