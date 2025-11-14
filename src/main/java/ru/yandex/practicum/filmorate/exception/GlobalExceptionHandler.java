package ru.yandex.practicum.filmorate.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Ошибки валидации из @Valid (@RequestBody и т.п.) -> 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest req
    ) {
        log.warn("400 Bad Request (MethodArgumentNotValidException): {}", ex.getMessage());
        return build(
                HttpStatus.BAD_REQUEST,
                "Validation error",
                ex.getMessage(),
                req.getRequestURI()
        );
    }

    /**
     * Наши кастомные ошибки валидации -> 400 Bad Request
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(
            ValidationException ex,
            HttpServletRequest req
    ) {
        log.warn("400 Bad Request (ValidationException): {}", ex.getMessage());
        return build(
                HttpStatus.BAD_REQUEST,
                "Validation error",
                ex.getMessage(),
                req.getRequestURI()
        );
    }

    /**
     * Ошибки "не найдено" -> 404 Not Found
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(
            NotFoundException ex,
            HttpServletRequest req
    ) {
        log.warn("404 Not Found: {}", ex.getMessage());
        return build(
                HttpStatus.NOT_FOUND,
                "Not Found",
                ex.getMessage(),
                req.getRequestURI()
        );
    }

    /**
     * Все прочие ошибки -> 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(
            Exception ex,
            HttpServletRequest req
    ) {
        log.error("500 Internal Server Error: {}", ex.getMessage(), ex);
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal error",
                ex.getMessage(),
                req.getRequestURI()
        );
    }

    /**
     * Утилита для сборки стандартного тела ответа
     */
    private ResponseEntity<Object> build(HttpStatus status, String error, String message, String path) {
        ErrorResponse body = new ErrorResponse(
                status.value(),
                error,
                message,
                path
        );
        return ResponseEntity.status(status).body(body);
    }
}
