package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }

    public Film create(Film film) {
        validateFilm(film);
        enrichFilm(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateFilm(film);
        enrichFilm(film);
        Film updated = filmStorage.update(film);
        return Optional.ofNullable(updated)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }

    public void addLike(Long filmId, Long userId) {
        findById(filmId);
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        findById(filmId);
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopular(int count) {
        return filmStorage.findPopular(count);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            throw new ru.yandex.practicum.filmorate.exception.ValidationException(
                    "Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    private void enrichFilm(Film film) {
        // 1. Проверяем и подтягиваем MPA из БД
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

        // если жанр не найден — кидаем 404
        Set<Genre> resolvedGenres = new LinkedHashSet<>();
        genresFromRequest.stream()
                .map(Genre::getId)
                .sorted() // чтобы был стабильный порядок по id
                .forEach(id -> {
                    Genre genre = genreStorage.findById(id)
                            .orElseThrow(() -> new NotFoundException("Жанр не найден: id=" + id));
                    resolvedGenres.add(genre); // LinkedHashSet сам уберёт дубли
                });

        film.setGenres(resolvedGenres);
    }

}