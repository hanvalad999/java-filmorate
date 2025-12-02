package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Film.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public class Film {
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не может превышать 200 символов")
    private String description;

    @NotNull(message = "Валидация не пройдена: не указана дата релиза")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительным числом")
    private int duration;

    @NotNull(message = "Рейтинг обязателен")
    private Mpa mpa;

    private Set<Genre> genres = new LinkedHashSet<>();

    @AssertTrue(message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    public boolean isReleaseDateValid() {
        return releaseDate == null || !releaseDate.isBefore(EARLIEST_RELEASE_DATE);
    }
}
