package ru.yandex.practicum.filmorate.model;

import java.util.Set;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

/**
 * User.
 */

@Data
@Builder
public class User {
    private Integer id;
    @Email
    @NonNull
    private String email;
    @NonNull
    @NotBlank
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
    private Set<Integer> friends;

}
