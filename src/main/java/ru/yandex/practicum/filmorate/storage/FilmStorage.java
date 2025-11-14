package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Film getFilmById(int id);

    List<Film> findAllFilms();

    List<Film> findTopFilms(int count);
}
