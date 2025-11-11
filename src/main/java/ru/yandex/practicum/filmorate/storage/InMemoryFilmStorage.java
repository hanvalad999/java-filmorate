package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new ConcurrentHashMap<>();
    private int idForFilm = 0;

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        filmValidation(film);
        film.setLikes(new HashSet<>());
        film.setId(getIdForFilm());
        films.put(film.getId(), film);
        log.info("Поступил запрос на добавление фильма. Фильм добавлен");
        return film;
    }

    @Override
    public Film update(Film film) {
        if (films.get(film.getId()) != null) {
            filmValidation(film);
            film.setLikes(new HashSet<>());
            films.put(film.getId(), film);
            log.info("Поступил запрос на изменения фильма. Фильм изменён.");
        } else {
            log.error("Поступил запрос на изменения фильма. Фильм не найден.");
            throw new NotFoundException("Film not found.");
        }
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else throw new NotFoundException("Film not found.");
    }

    private int getIdForFilm() {
        return ++idForFilm;
    }

    private void filmValidation(Film film) throws ValidationException {
        if (film == null) {
            throw new ValidationException("Тело запроса не должно быть пустым");
        }

        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Некорректно указано название фильма.");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Превышено количество символов в описании фильма.");
        }

        if (film.getReleaseDate() == null ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)) ||
                film.getReleaseDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Некорректно указана дата релиза.");
        }
    }
}
