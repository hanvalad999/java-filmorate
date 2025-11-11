package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film f);
    Film update(Film f);
    Optional<Film> findById(long id);
    Collection<Film> findAll();
    void deleteById(long id);
}
