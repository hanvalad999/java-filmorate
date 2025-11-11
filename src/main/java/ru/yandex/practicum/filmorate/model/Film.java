package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate; // ← добавь
import java.util.Set;                                // ← добавь

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public class Film {
    private int id;
    @NotBlank
    @NonNull
    private String name;
    @Size(max = 200)
    private String description;
    @Past
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private Set<Integer> likes;
}
