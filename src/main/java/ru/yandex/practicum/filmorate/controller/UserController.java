package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new ConcurrentHashMap<>();

    @GetMapping
    public Collection<User> findUser() {
        log.info("Получен запрос GET /users. Количество пользователей сейчас: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User createUsers(@Valid @RequestBody User user) {
        log.info("Попытка создать пользователя: {}", user);

        normalize(user);
        user.setId(getNextId());
        users.put(user.getId(), user);

        log.info("Пользователь успешно создан: id={}, name='{}'", user.getId(), user.getName());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Попытка обновить пользователя: {}", user);

        if (!existsUser(user)) {
            log.warn("Обновление не удалось: пользователь с id={} не найден", user.getId());
            throw new ValidationException("Пользователь с таким ID не найден для обновления");
        }

        normalize(user);
        users.put(user.getId(), user);

        log.info("Пользователь успешно обновлён: id={}, name='{}'", user.getId(), user.getName());
        return user;
    }

    private boolean existsUser(User user) {
        return user != null && user.getId() != null && users.containsKey(user.getId());
    }

    private void normalize(User user) {
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }

    }



    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return currentMaxId + 1;
    }
}
