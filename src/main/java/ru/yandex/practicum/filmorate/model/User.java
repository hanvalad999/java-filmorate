package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;


import java.time.LocalDate;

/**
 * User.
 */

@Data
public class User {
    private Long id;

    @Email(message = "Некорректный формат email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;

    @NotBlank(message = "Логин не может быть пустым или содержать пробелы")
    private String login;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotNull(message = "Дата рождения обязательна")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;


}
