package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Унифицированное тело ошибки, возвращаемое из GlobalExceptionHandler.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {

    /** HTTP-код ошибки (например, 404, 400, 500) */
    private int status;

    /** Краткое название ошибки (например, "Not Found", "Validation error") */
    private String error;

    /** Подробное сообщение — что именно пошло не так */
    private String message;

    /** URI запроса, где произошла ошибка */
    private String path;
}
