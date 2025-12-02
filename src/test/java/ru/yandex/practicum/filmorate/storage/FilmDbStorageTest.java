package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, UserDbStorage.class, GenreDbStorage.class, MpaDbStorage.class})
class FilmDbStorageTest {

    @Autowired
    private FilmStorage filmStorage;

    @Autowired
    private UserStorage userStorage;

    @Test
    void shouldCreateAndFindFilm() {
        Film film = buildFilm();
        Film saved = filmStorage.create(film);

        Optional<Film> found = filmStorage.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getMpa().getName()).isEqualTo("G");
        assertThat(found.get().getGenres())
                .extracting(Genre::getId)
                .containsExactly(1, 2);
    }

    @Test
    void shouldUpdateFilm() {
        Film film = filmStorage.create(buildFilm());

        film.setName("Updated film");
        film.setDescription("New description");
        film.setMpa(new Mpa(2, null));
        film.setGenres(Set.of(new Genre(3, null)));

        Film updated = filmStorage.update(film);

        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("Updated film");
        assertThat(updated.getDescription()).contains("New");
        assertThat(updated.getMpa().getId()).isEqualTo(2);
        assertThat(updated.getGenres())
                .extracting(Genre::getId)
                .containsExactly(3);
    }

    @Test
    void shouldFindPopularByLikes() {
        Film film = filmStorage.create(buildFilm());
        User user = userStorage.create(buildUser());

        filmStorage.addLike(film.getId(), user.getId());

        List<Film> popular = filmStorage.findPopular(5);
        assertThat(popular).isNotEmpty();
        assertThat(popular.getFirst().getId()).isEqualTo(film.getId());
    }

    private Film buildFilm() {
        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1, null));
        film.setGenres(new LinkedHashSet<>(Set.of(new Genre(1, null), new Genre(2, null))));
        return film;
    }

    private User buildUser() {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setLogin("login");
        user.setName("User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }
}