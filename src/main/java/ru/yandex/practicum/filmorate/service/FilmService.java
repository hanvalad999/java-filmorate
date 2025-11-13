package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    // --- лайки ---

    public void like(int filmId, int userId) {
        // проверяем, что фильм существует
        Film film = filmStorage.getFilmById(filmId);
        // проверяем, что пользователь существует
        userStorage.getUserById(userId);
        // ставим лайк
        film.getLikes().add(userId);
    }

    public void deleteLike(int filmId, int userId) {
        // фильм существует?
        Film film = filmStorage.getFilmById(filmId);
        // пользователь существует?
        userStorage.getUserById(userId);

        // если лайка не было — 404
        if (!film.getLikes().remove(userId)) {
            throw new NotFoundException(
                    "Пользователь " + userId + " не ставил лайк фильму " + filmId + "."
            );
        }
    }

    // --- остальной сервис, как у тебя было ---

    public List<Film> getTopFilms(int count) {
        return filmStorage.findAllFilms().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
