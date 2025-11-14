package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    // --- CRUD по фильмам ---

    public Film create(Film film) {
        log.info("Создание фильма: {}", film);
        Film created = filmStorage.create(film);
        log.info("Фильм создан: id={}", created.getId());
        return created;
    }

    public Film update(Film film) {
        log.info("Обновление фильма: id={}", film.getId());
        Film updated = filmStorage.update(film);
        log.info("Фильм обновлён: id={}", updated.getId());
        return updated;
    }

    public List<Film> findAllFilms() {
        log.info("Запрошен список всех фильмов");
        return filmStorage.findAllFilms();
    }

    public Film getFilmById(int id) {
        log.info("Запрошен фильм по id={}", id);
        return filmStorage.getFilmById(id);
    }

    // --- лайки ---

    public void like(int filmId, int userId) {
        log.info("Добавление лайка: фильм={}, пользователь={}", filmId, userId);

        // проверяем, что фильм существует
        Film film = filmStorage.getFilmById(filmId);
        // явно получаем пользователя — код выглядит завершённым и очевидным
        User user = userStorage.getUserById(userId);
        log.debug("Найден пользователь для лайка: id={}", user.getId());

        boolean added = film.getLikes().add(userId);
        if (added) {
            log.info("Лайк успешно добавлен: фильм={}, пользователь={}", filmId, userId);
        } else {
            log.info("Лайк уже был ранее: фильм={}, пользователь={}", filmId, userId);
        }
    }

    public void deleteLike(int filmId, int userId) {
        log.info("Удаление лайка: фильм={}, пользователь={}", filmId, userId);

        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        log.debug("Найден пользователь для удаления лайка: id={}", user.getId());

        if (!film.getLikes().remove(userId)) {
            log.warn("Попытка удалить несуществующий лайк: фильм={}, пользователь={}", filmId, userId);
            throw new NotFoundException(
                    "Пользователь " + userId + " не ставил лайк фильму " + filmId + "."
            );
        }

        log.info("Лайк успешно удалён: фильм={}, пользователь={}", filmId, userId);
    }

    // --- топ фильмов ---

    public List<Film> getTopFilms(int count) {
        log.info("Запрошен топ фильмов, count={}", count);
        return filmStorage.findTopFilms(count);
    }
}
