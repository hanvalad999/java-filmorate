package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private final FilmController controller = new FilmController();

    // helper: делаем строку любой длины
    private String repeatChar(char ch, int count) {
        return String.valueOf(ch).repeat(count);
    }

    @Test
    void createFilms_nullBody_shouldThrow() {
        assertThrows(
                ValidationException.class,
                () -> controller.createFilms(null),
                "Ожидалось, что null-тело не пройдёт"
        );
    }

    @Test
    void createFilms_blankName_shouldThrow() {
        Film film = new Film();
        film.setName("   "); // пустое имя
        film.setDescription("norm");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(
                ValidationException.class,
                () -> controller.createFilms(film),
                "Ожидалось, что пустое название не пройдёт"
        );
    }

    @Test
    void createFilms_descriptionLongerThan200_shouldThrow() {
        Film film = new Film();
        film.setName("Matrix");
        film.setDescription(repeatChar('a', 201)); // 201 символ
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(
                ValidationException.class,
                () -> controller.createFilms(film),
                "Ожидалось, что описание >200 символов не пройдёт"
        );
    }

    @Test
    void createFilms_descriptionExactly200_shouldPass() {
        Film film = new Film();
        film.setName("Matrix");
        film.setDescription(repeatChar('a', 200)); // ровно лимит
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertDoesNotThrow(
                () -> controller.createFilms(film),
                "Описание длиной 200 должно быть допустимо"
        );
    }

    @Test
    void createFilms_releaseDateBeforeEarliest_shouldThrow() {
        Film film = new Film();
        film.setName("Old Film");
        film.setDescription("desc");
        // раньше 28.12.1895
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(10);

        assertThrows(
                ValidationException.class,
                () -> controller.createFilms(film),
                "Фильм слишком старый не должен пройти"
        );
    }

    @Test
    void createFilms_releaseDateAtBoundary_shouldPass() {
        Film film = new Film();
        film.setName("Lumiere Brothers");
        film.setDescription("First movie ever?");
        // граничная дата 28.12.1895
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(1);

        assertDoesNotThrow(
                () -> controller.createFilms(film),
                "Дата релиза = 28.12.1895 должна считаться валидной"
        );
    }

    @Test
    void createFilms_nonPositiveDuration_shouldThrow() {
        Film filmZero = new Film();
        filmZero.setName("Bad duration");
        filmZero.setDescription("desc");
        filmZero.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmZero.setDuration(0);

        assertThrows(
                ValidationException.class,
                () -> controller.createFilms(filmZero),
                "Длительность 0 не должна проходить"
        );

        Film filmNegative = new Film();
        filmNegative.setName("Bad duration");
        filmNegative.setDescription("desc");
        filmNegative.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmNegative.setDuration(-5);

        assertThrows(
                ValidationException.class,
                () -> controller.createFilms(filmNegative),
                "Отрицательная длительность не должна проходить"
        );
    }

    @Test
    void createFilms_validFilm_shouldAssignIdAndStore() {
        Film film = new Film();
        film.setName("Interstellar");
        film.setDescription("Space and time");
        film.setReleaseDate(LocalDate.of(2014, 11, 7));
        film.setDuration(169);

        Film created = controller.createFilms(film);

        assertNotNull(created.getId(), "После сохранения у фильма должен быть id");
        assertEquals(1L, created.getId(), "Первый добавленный фильм должен получить id=1");
        assertEquals("Interstellar", created.getName());
    }
}
