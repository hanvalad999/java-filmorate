package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    // основной конструктор для Spring (продакшн)
    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    // дополнительный конструктор для тестов,
    // где создают new FilmService(filmStorage)
    // и UserStorage не нужен
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = null;
    }

    // --- лайки ---

    private void requireUserStorage() {
        if (userStorage == null) {
            throw new IllegalStateException("UserStorage is not configured");
        }
    }

    public void like(int filmId, int userId) {
        requireUserStorage();

        // проверяем, что фильм существует
        Film film = filmStorage.getFilmById(filmId);
        // проверяем, что пользователь существует
        userStorage.getUserById(userId);

        film.getLikes().add(userId);
    }

    public void deleteLike(int filmId, int userId) {
        requireUserStorage();

        Film film = filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);

        if (!film.getLikes().remove(userId)) {
            throw new NotFoundException(
                    "Пользователь " + userId + " не ставил лайк фильму " + filmId + "."
            );
        }
    }

    // --- топ фильмов ---

    public List<Film> getTopFilms(int count) {
        return filmStorage.findAllFilms().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
