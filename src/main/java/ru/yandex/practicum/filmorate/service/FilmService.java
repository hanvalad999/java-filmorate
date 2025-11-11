package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage films;
    private final UserStorage users; // чтобы проверять существование user при лайке

    public Collection<Film> findAll() {
        return films.findAll();
    }

    public Film get(long id) {
        return films.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Film with id=" + id + " not found"));
    }

    public Film create(Film f) {
        validate(f, /*isUpdate=*/false);
        return films.create(f);
    }

    public Film update(Film f) {
        if (f.getId() == null) {
            throw new ValidationException("Id обязателен для обновления фильма");
        }
        // убедимся, что фильм существует
        get(f.getId());
        validate(f, /*isUpdate=*/true);
        return films.update(f);
    }

    public void like(long filmId, long userId) {
        Film film = get(filmId);
        // проверим, что юзер существует (404, если нет)
        users.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id=" + userId + " not found"));
        film.getLikes().add(userId); // Set защитит от повторного лайка
    }

    public void unlike(long filmId, long userId) {
        Film film = get(filmId);
        film.getLikes().remove(userId);
    }

    public List<Film> top(int count) {
        if (count <= 0) count = 10;
        return films.findAll().stream()
                .sorted(Comparator.comparingInt((Film x) -> x.getLikes().size()).reversed()
                        .thenComparing(Film::getId)) // стабильность при равенстве лайков
                .limit(count)
                .toList();
    }

    private void validate(Film f, boolean isUpdate) {
        if (f == null) {
            throw new ValidationException("Тело фильма не должно быть пустым");
        }

        // name — обязательно и непустое
        if (f.getName() == null || f.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым");
        }

        // description — не длиннее 200
        if (f.getDescription() != null && f.getDescription().length() > 200) {
            throw new ValidationException("Описание не может превышать 200 символов");
        }

        // releaseDate — обязателен и не раньше 28.12.1895
        if (f.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза обязательна");
        }
        if (f.getReleaseDate().isBefore(MIN_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }

        // duration — > 0
        if (f.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительной");
        }
    }
}
