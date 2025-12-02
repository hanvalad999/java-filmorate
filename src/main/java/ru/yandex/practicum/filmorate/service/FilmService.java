package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public List<Film> findAll() {
        List<Film> films = filmStorage.findAll();
        log.info("Получен запрос на список фильмов. Текущее количество: {}", films.size());
        return films;
    }

    public Film findById(Long id) {
        Film film = filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        log.info("Фильм найден: id={}, name='{}'", film.getId(), film.getName());
        return film;
    }

    public Film create(Film film) {
        log.info("Попытка создать фильм: {}", film);
        validateFilm(film);
        enrichFilm(film);
        Film created = filmStorage.create(film);
        log.info("Фильм успешно создан: id={}, name='{}'", created.getId(), created.getName());
        return created;
    }

    public Film update(Film film) {
        log.info("Попытка обновить фильм: {}", film);
        validateFilm(film);
        enrichFilm(film);
        Film updated = filmStorage.update(film);
        Film resolved = Optional.ofNullable(updated)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        log.info("Фильм успешно обновлён: id={}, name='{}'", resolved.getId(), resolved.getName());
        return resolved;

    }

    public void addLike(Long filmId, Long userId) {
        ensureFilmExists(filmId);
        ensureUserExists(userId);

        log.info("Пользователь {} ставит лайк фильму {}", userId, filmId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        ensureFilmExists(filmId);
        ensureUserExists(userId);

        log.info("Пользователь {} удаляет лайк у фильма {}", userId, filmId);
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopular(int count) {
        List<Film> popular = filmStorage.findPopular(count);
        log.info("Получен список популярных фильмов: запрошено {}, возвращено {}", count, popular.size());
        return popular;
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            throw new ru.yandex.practicum.filmorate.exception.ValidationException(
                    "Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    private void enrichFilm(Film film) {
        // 1. Проверяем MPA
        Mpa mpa = mpaStorage.findById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("Рейтинг не найден"));
        film.setMpa(mpa);

        // 2. Проверяем жанры
        Set<Genre> genresFromRequest = Optional.ofNullable(film.getGenres())
                .orElseGet(LinkedHashSet::new);

        if (genresFromRequest.isEmpty()) {
            film.setGenres(new LinkedHashSet<>());
            return;
        }

        Set<Integer> ids = genresFromRequest.stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        // ОДИН запрос вместо N
        Set<Genre> resolvedGenres = genreStorage.findByIds(ids);

        // если количество найденных != количество запрошенных → кто-то не найден
        if (resolvedGenres.size() != ids.size()) {
            throw new NotFoundException("Один или несколько жанров не найдены");
        }

        // Сортируем по id и ставим в фильм
        film.setGenres(resolvedGenres.stream()
                .sorted(Comparator.comparingInt(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    private Film ensureFilmExists(Long filmId) {
        return filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }

    private User ensureUserExists(Long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

}